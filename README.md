![Build](https://github.com/dowhilenotfalse/bertlv_kotlin_mpp/workflows/Build/badge.svg)

# BERTLV Kotlin Multiplatform

Library is used to work with ASN.1 BER-TLV structures and available in Kotlin, Java, Javascript, and Native. It supports multiple bytes tags and length, and both primitive and constructed TLVs. Payment processing with EMV is a common use case for using this library.

## Features
* Build and parse TLVs
* Add, delete, and update tags

## Availability
* Kotlin
* Java
* Javascript
* Native


## Usage


#### Parse TLV

```
      BerTlv("8A0C48656c6c6f20576f726c64210C010A")
      val tag = berTlv.tag("8A")
      
      tag?.stringValue()      // Hello World!
      tag?.hexValue()         // 48656c6c6f20576f726c6421
      tag?.toString()         // 8A0C48656c6c6f20576f726c6421
```

#### Build TLV

```
      val berTlv = BerTlv()
      berTlv.addTag(Tag("8A", "00"))
      
      berTlv.toString()       // 8A0100
```


#### Add Tag

```
      val berTlv = BerTlv()
      
      berTlv.addTag(Tag("8A", "00"))
      berTlv.addTag(Tag(0x9F02, "00"))
      
      berTlv.toString()       // 8A01009F020100
```

#### Update Tag

```
      val berTlv = BerTlv("8A00010C010A")
         
      var tag = berTlv.tag("8A")
      tag?.setHexValue("05")

      tag?.toString()          // 8A0105
      
      tag = berTlv.tag("0C")
      tag?.setStringValue("Hello World!")
      
      tag?.toString()          // 0C0C48656c6c6f20576f726c6421
```

#### Delete Tag

```
      val berTlv = BerTlv("8A0C48656c6c6f20576f726c64210C010A")      
      berTlv.deleteTag("C0")
      
      berTlv.toString()       // 8A0C48656c6c6f20576f726c6421
```

## Version History

* 1.0-SNAPSHOT
    * Initial Release

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
