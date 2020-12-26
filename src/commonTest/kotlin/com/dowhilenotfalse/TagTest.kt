package com.dowhilenotfalse

import kotlin.test.*


internal class TagTest {

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun tagSupportedAsInt(){
        val tag = Tag(0x9F02, "00010203")
        assertEquals("9F02", tag.name)
    }

    @Test
    fun tagSupportedAsHexString(){
        val tag = Tag("9F02", "00010203")
        assertEquals("9F02", tag.name)
    }

    @Test
    fun valueSupportedAsString(){
        val value = "FFAAFF"
        val tag = Tag("9F02", "00010203")
        tag.setHexValue(value)

        assertEquals(value, BerTlv.hex(tag.value))
    }

    @Test
    fun isConstructedWhenTagConstructedIsTrue(){
        val tag = Tag("30", "0C 06 4D 7E 6C 6C 65 72 02 01 1E 01 01 00 80 01 00")
        assertTrue(tag.isConstructed())
    }

    @Test
    fun isUniversalClassWhenUniveralClassIsTrue(){
        val tag = Tag(0x1F, "0C 06")
        assertTrue(tag.isUniversalClass())
    }

    @Test
    fun isApplicationClassWhenApplicationClassIsTrue(){
        val tag = Tag(0x4F, "0C 06")
        assertTrue(tag.isApplicationClass())
    }

    @Test
    fun isContextClassWhenContextClassIsTrue(){
        val tag = Tag(0x8F, "0C 06")
        assertTrue(tag.isContextClass())
    }

    @Test
    fun isPrivateClassWhenPrivateClassIsTrue(){
        val tag = Tag(0xCF, "0C 06")
        assertTrue(tag.isPrivateClass())
    }

    @Test
    fun hexValueReturnsHexString(){
        val value = "48656C6C6F20576F726C6421"
        val tag = Tag(0x0C, value)
        assertEquals(value, tag.hexValue())
    }

    @Test
    fun setStringValueConvertsStringToValue(){
        val tag = Tag(0x0C, "48 65 6c 6c 6f 20 57 6f 72 6c 64 21")
        val value = "Hello World!"
        tag.setStringValue(value)

        assertEquals(value, tag.stringValue())
    }

    @Test
    fun stringValueReturnsString(){
        val tag = Tag(0x0C, "48 65 6c 6c 6f 20 57 6f 72 6c 64 21")
        assertEquals("Hello World!", tag.stringValue())
    }

    @Test
    fun toStringConstructsTLV(){
        val tag = Tag("30", "00010203")
        assertEquals("300400010203", tag.toString())
    }

    @Test
    fun equalsWhenOtherObjectDifferentClassIsFalse(){
        val tag = Tag("30", "00010203")
        assertFalse(tag.equals(""))
    }

    @Test
    fun equalsWhenTagSizeDifferentIsFalse(){
        val tag = Tag("30", "00010203")
        val other = Tag("9F02", "00010203")
        assertNotEquals(tag, other)
    }

    @Test
    fun equalsWhenValueSizeDifferentIsFalse(){
        val tag = Tag("30", "00010203")
        val other = Tag("30", "0001020321")
        assertNotEquals(tag, other)
    }

    @Test
    fun equalsWhenValueBytesDifferentOrderIsFalse(){
        val tag = Tag("30", "00010203")
        val other = Tag("30", "00010302")
        assertNotEquals(tag, other)
    }

    @Test
    fun equalsWhenTagEquivalentIsTrue(){
        val tag = Tag("30", "00010203")
        val other = Tag("30", "00010203")
        assertEquals(tag, other)
    }

}