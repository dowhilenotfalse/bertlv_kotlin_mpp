[![Android-master Actions Status](https://github.com/dowhilenotfalse/bertlv_kotlin_mpp/workflows/CI/badge.svg)](https://github.com/dowhilenotfalse/bertlv_kotlin_mpp/actions)

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


## Getting Started

Parse TLV
------------

```
      BerTlv("30110C064D7E6C6C657202011E010100800100")
```

Build TLV
------------

```
      val berTlv = BerTlv()
      berTlv.addTag(Tag("8A", "00"))
      
      val tlv = berTlv.toString()
```


Update TLV
------------

```
      val berTlv = BerTlv()
      
      //add
      berTlv.addTag(Tag("8A", "00"))
      berTlv.addTag(Tag("9F02", "00"))
      
      //delete
      berTlv.deleteTag("9F02")
      
      //update
      val tag = berTlv.tag("8A")
      tag?.setValue("05")

      val tlv = berTlv.toString()
```

## Version History

* 1.0-SNAPSHOT
    * Initial Release

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
