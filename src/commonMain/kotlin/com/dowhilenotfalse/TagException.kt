package com.dowhilenotfalse

class TagException(val tag: Tag, val intRange: IntRange): Exception(){
    override fun toString() = "TagException: Tag: ${tag.name} Range: $intRange"
}