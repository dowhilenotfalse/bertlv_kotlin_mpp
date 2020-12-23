package com.dowhilenotfalse

class Tag(val tag: IntArray, var value: IntArray){
    val name = BerTlv.hexString(tag)
    val children = mutableMapOf<String, Tag>()

    constructor(tagHex: String, valueHex: String) : this(BerTlv.byteArray(tagHex), BerTlv.byteArray(valueHex))

    fun setValue(valueHex: String){ value = BerTlv.byteArray(valueHex) }
    fun isConstructed() = (tag[0] and BerTlv.TAG_PRIMITIVE_INDICATOR) == 32
    fun hasChildren() = children.isNotEmpty()

    override fun toString(): String {
        return "${BerTlv.hexString(tag)}${BerTlv.hexString(BerTlv.length(value))}${BerTlv.hexString(value)}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Tag) return false
        if (tag.size != other.tag.size) return false
        if (value.size != other.value.size) return false
        for (i in tag.indices) { if (tag[i] != other.tag[i]) return false }
        for (i in value.indices) { if (value[i] != other.value[i]) return false }

        return true
    }
}
