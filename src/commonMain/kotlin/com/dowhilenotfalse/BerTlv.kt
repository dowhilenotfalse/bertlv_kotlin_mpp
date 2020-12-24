package com.dowhilenotfalse

import kotlin.math.ceil

class BerTlv(private val hexString: String) {
    val tags = mutableMapOf<String, Tag>()

    init {
        val byteArray = byteArray(hexString)
        parseTags(byteArray)
    }

    constructor() : this("")

    fun deleteTag(tag: Tag) = tags.remove(tag.name)
    fun deleteTag(tagName: String) = tags.remove(tagName)
    fun addTag(tag: Tag) { tags[tag.name] = tag }
    fun tag(tagName: String) = tags[tagName]

    private fun parseTags(byteArray: IntArray, parentTag: Tag? = null){
        var startIndex = 0
        while(hasNextTag(byteArray, startIndex)){
            val tagResult = parseNextTag(byteArray, startIndex)
            val tag = tagResult.tag

            if(parentTag != null) parentTag.children[tag.name] = tag
            else tags[tag.name] = tag
            startIndex = tagResult.range.last
            if(tag.isConstructed()){ parseTags(tag.value, tag) }
        }
    }

    private fun parseNextTag(tlv: IntArray, startIndex: Int): TagResult{
        var tagByteCount = 1
        val firstByte = tlv[startIndex]
        var tagMultiByte = (firstByte and TAG_MULTIBYTE_INDICATOR) == TAG_MULTIBYTE_INDICATOR

        while (tagMultiByte){
            val nextByte = tlv[startIndex + tagByteCount]
            tagMultiByte = (nextByte and TAG_MULTIBYTE_END_INDICATOR) != 0
            tagByteCount++
        }

        val tagByteLength = startIndex + tagByteCount
        val valueResult = parseTagValue(tlv, tagByteLength)

        val tag = Tag(
            tlv.copyOfRange(startIndex, tagByteLength),
            valueResult.byteArray
        )

        return TagResult(tag, IntRange(startIndex, valueResult.range.last))
    }

    private fun parseTagValue(tlv: IntArray, startIndex: Int): ByteResult{
        if (tlv.size <= startIndex) return ByteResult()

        val firstByte = tlv[startIndex]
        val multiByteLength = (firstByte and LENGTH_MULTIBYTE_INDICATOR) == LENGTH_MULTIBYTE_INDICATOR
        var valueLength = (firstByte and LENGTH_VALUE_BITS)
        val valueStartIndex = if(multiByteLength) startIndex + valueLength + 1 else startIndex + 1

        if(tlv.size < valueStartIndex + valueLength) return ByteResult()

        if (multiByteLength && valueLength > 0) {
            val multiByteLengthStartIndex = startIndex + 2
            val lengthByteCount = valueLength
            var nextByte = tlv[multiByteLengthStartIndex]
            valueLength = nextByte

            for (i in 1 until lengthByteCount) {
                nextByte = tlv[multiByteLengthStartIndex + i]
                valueLength = valueLength and nextByte
                valueLength.shl(8)
            }
        }


        return ByteResult(
            tlv.copyOfRange(valueStartIndex, valueStartIndex + valueLength),
            IntRange(startIndex, valueStartIndex + valueLength)
        )
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        tags.forEach { stringBuilder.append(it.value) }
        return stringBuilder.toString()
    }

    private fun hasNextTag(tlv: IntArray, startIndex: Int) = tlv.size - startIndex >= TLV_MINIMUM_BYTE_COUNT
    inner class ByteResult(val byteArray: IntArray = intArrayOf(), val range: IntRange = IntRange(0,0))
    inner class TagResult(val tag: Tag, val range: IntRange = IntRange(0,0))

    companion object{
        const val TLV_MINIMUM_BYTE_COUNT = 3
        const val TAG_PRIMITIVE_INDICATOR = 32
        const val TAG_MULTIBYTE_INDICATOR = 31
        const val TAG_MULTIBYTE_END_INDICATOR = 128
        const val LENGTH_MULTIBYTE_INDICATOR = 128
        const val LENGTH_VALUE_BITS = 127

        fun byteArray(hexString: String): IntArray{
            val trimmedHexString = hexString.replace("\\s".toRegex(), "")

            if(trimmedHexString.isEmpty() || trimmedHexString.length % 2 > 0) return intArrayOf()
            val byteArray = IntArray(trimmedHexString.length/2)

            for(i in trimmedHexString.indices step 2){
                byteArray[i/2] =  trimmedHexString.substring(i,i+2).toInt(16)
            }

            return byteArray
        }

        fun hexString(byteArray: IntArray): String{
            val stringBuilder = StringBuilder()
            byteArray.forEach {
                val hex = it.toString(16)
                if(hex.length % 2 != 0) stringBuilder.append("0")
                stringBuilder.append(hex)
            }

            return stringBuilder.toString().toUpperCase()
        }

        fun length(tagValue: IntArray): IntArray{
            if(tagValue.isEmpty()) return intArrayOf(0)
            if(tagValue.size <= LENGTH_VALUE_BITS) return intArrayOf( tagValue.size)

            val numberOfLengthBytes = ceil(tagValue.size/255f).toInt()
            val lengthByteArray = IntArray(numberOfLengthBytes + 1)
            for(i in 0 until numberOfLengthBytes){
                val lengthPart = tagValue.size.shr(i*8) and 255
                lengthByteArray[i] = lengthPart
            }

            lengthByteArray[numberOfLengthBytes] = LENGTH_MULTIBYTE_INDICATOR + numberOfLengthBytes

            return lengthByteArray.reversedArray()

        }
    }
}