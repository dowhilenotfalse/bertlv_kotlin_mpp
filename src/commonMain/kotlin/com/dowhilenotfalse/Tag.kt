package com.dowhilenotfalse

class Tag(val id: IntArray, var value: IntArray = intArrayOf()){
    val name = BerTlv.hex(id)
    val children = mutableMapOf<String, Tag>()

    constructor(tagHex: String, valueHex: String = "") : this(BerTlv.bytes(tagHex), BerTlv.bytes(valueHex))
    constructor(tag: Int, valueHex: String = "") : this(BerTlv.bytes(tag), BerTlv.bytes(valueHex))

    fun isConstructed() = (id[0] and BerTlv.TAG_PRIMITIVE_INDICATOR) == 32
    fun isUniversalClass() = (id[0] and BerTlv.TAG_CLASS_INDICATOR) == 0
    fun isApplicationClass() = (id[0] and BerTlv.TAG_CLASS_INDICATOR) == 64
    fun isContextClass() = (id[0] and BerTlv.TAG_CLASS_INDICATOR) == 128
    fun isPrivateClass() = (id[0] and BerTlv.TAG_CLASS_INDICATOR) == 192
    fun hasChildren() = children.isNotEmpty()
    fun setHexValue(valueHex: String){ value = BerTlv.bytes(valueHex) }
    fun hexValue() = BerTlv.hex(value)

    fun setStringValue(value: String): IntArray {
        val array = IntArray(value.length)
        for(i in value.indices){ array[i] = value[i].toInt()}

        return array
    }

    fun stringValue(): String{
        val stringBuilder = StringBuilder(value.size)
        value.forEach { stringBuilder.append(it.toChar()) }

        return stringBuilder.toString()
    }

    override fun toString(): String {
        return "${BerTlv.hex(id)}${BerTlv.hex(BerTlv.length(value))}${BerTlv.hex(value)}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Tag) return false
        if (id.size != other.id.size) return false
        if (value.size != other.value.size) return false
        for (i in id.indices) { if (id[i] != other.id[i]) return false }
        for (i in value.indices) { if (value[i] != other.value[i]) return false }

        return true
    }

    override fun hashCode(): Int {
        var result = id.contentHashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}
