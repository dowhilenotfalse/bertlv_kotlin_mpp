package com.dowhilenotfalse

import kotlin.math.ceil


class BerTlv @Throws(TagException::class) constructor(byteArray: IntArray) {
    val tags = mutableMapOf<String, Tag>()

    init {
        parseTags(byteArray)
    }

    constructor() : this("")
    @Throws(TagException::class) constructor(hexString: String): this(bytes(hexString))

    fun deleteTag(tag: Tag) = tags.remove(tag.name)
    fun deleteTag(tagName: String) = tags.remove(tagName)
    fun addTag(tag: Tag) { tags[tag.name] = tag }
    fun tag(tagName: String) = tags[tagName]

    private fun parseTags(byteArray: IntArray, parentTag: Tag? = null){
        var startIndex = 0

        if(byteArray.isNotEmpty() && !hasNextTag(byteArray, startIndex)) throw TagException(Tag(byteArray), IntRange(0, byteArray.size))

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

        while (tagMultiByte && tagByteCount < tlv.size){
            val nextByte = tlv[startIndex + tagByteCount]
            tagMultiByte = (nextByte and TAG_MULTIBYTE_END_INDICATOR) != 0
            tagByteCount++
        }

        val tagByteLength = startIndex + tagByteCount
        val tagBytesTooLong = tagByteLength >= tlv.size
        val tagId = if(tagBytesTooLong) tlv.copyOfRange(startIndex, tlv.size) else tlv.copyOfRange(startIndex, tagByteLength)
        if(tagBytesTooLong) throw TagException(Tag(tagId), IntRange(startIndex, tlv.size))

        val valueResult = parseTagValue(tlv, tagByteLength)
        if(valueResult.failure) throw TagException(Tag(tagId), valueResult.range)

        val tag = Tag(tagId, valueResult.byteArray)

        return TagResult(tag, IntRange(startIndex, valueResult.range.last))
    }

    private fun parseTagValue(tlv: IntArray, startIndex: Int): ByteResult{
        val firstByte = tlv[startIndex]
        val multiByteLength = (firstByte and LENGTH_MULTIBYTE_INDICATOR) == LENGTH_MULTIBYTE_INDICATOR
        var valueLength = (firstByte and LENGTH_VALUE_BITS)
        val valueStartIndex = if(multiByteLength) startIndex + valueLength + 1 else startIndex + 1

        val valueBytesTooLong = valueStartIndex + valueLength > tlv.size
        if(valueBytesTooLong)
            return ByteResult(range = IntRange(startIndex, tlv.size), failure = true)

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

        val value = tlv.copyOfRange(valueStartIndex, valueStartIndex + valueLength)

        return ByteResult(value, IntRange(startIndex, valueStartIndex + valueLength))
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        tags.forEach { stringBuilder.append(it.value) }
        return stringBuilder.toString()
    }

    private fun hasNextTag(tlv: IntArray, startIndex: Int) = tlv.size - startIndex >= TLV_MINIMUM_BYTE_COUNT
    inner class ByteResult(val byteArray: IntArray = intArrayOf(), val range: IntRange = IntRange(0,0), val failure: Boolean = false)
    inner class TagResult(val tag: Tag, val range: IntRange = IntRange(0,0))

    companion object{
        const val TLV_MINIMUM_BYTE_COUNT = 3
        const val TAG_PRIMITIVE_INDICATOR = 32
        const val TAG_CLASS_INDICATOR = 192
        const val TAG_MULTIBYTE_INDICATOR = 31
        const val TAG_MULTIBYTE_END_INDICATOR = 128
        const val LENGTH_MULTIBYTE_INDICATOR = 128
        const val LENGTH_VALUE_BITS = 127

        fun bytes(value: Int): IntArray{
            var number = value
            val numberOfBytes = when{
                number <= 255 -> 1
                number <= 65535 -> 2
                number <= 16777215 -> 3
                else -> 4
            }

            val byteArray = IntArray(numberOfBytes)

            for(i in numberOfBytes - 1 downTo 0){
                byteArray[i] = number and 255
                number = number.shr(8)
            }

            return byteArray
        }

        fun bytes(hexString: String): IntArray{
            val trimmedHexString = hexString.replace("\\s".toRegex(), "")
            if(trimmedHexString.isEmpty() || trimmedHexString.length % 2 > 0) return intArrayOf()
            val byteArray = IntArray(trimmedHexString.length/2)

            for(i in trimmedHexString.indices step 2){
                byteArray[i/2] =  trimmedHexString.substring(i,i+2).toInt(16)
            }

            return byteArray
        }

        fun hex(byteArray: IntArray): String{
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