![Build](https://github.com/dowhilenotfalse/bertlv_kotlin_mpp/workflows/Build/badge.svg)

# BERTLV Kotlin Multiplatform

Library is used to work with ASN.1 BER-TLV structures and available in Kotlin, Java, Javascript, and Native. It supports multiple bytes tags and length, and both primitive and constructed TLVs. Payment processing with EMV is a common use case for using this library.

## Features
* Build and parse TLVs
* Add, delete, and update tags


## Availability
* [Kotlin](##Kotlin)
* [Java](##Java)
* Javascript
* Native


## Kotlin

### Gradle
```
dependencies {
  implementation 'com.dowhilenotfalse:bertlv-jvm:1.0.0'
}
```

### Example
```
@Throws(TagException::class)
fun main(args: Array<String>) {
    val berTlv = BerTlv()

    //Add Multiple Tags
    berTlv.addTags("8A01000C050101010101")

    //Update Tag
    val tag8A = berTlv.tag("8A")
    tag8A!!.setStringValue("Hello World")

    //Add Tag
    val newTag = Tag("9F02", "0A1105")
    berTlv.addTag(newTag)

    //Delete Tag
    berTlv.deleteTag("0C")


    println("Tag List: " + berTlv.tags) 
    //Tag List: {8A=8A0B48656C6C6F20576F726C64, 9F02=9F02030A1105}
    println("Tags: $berTlv")
    //Tags: 8A0B48656C6C6F20576F726C649F02030A1105
    println("Tag " + tag8A.name + ": " + tag8A.toString())
    //Tag 8A: 8A0B48656C6C6F20576F726C64
    println("Tag " + tag8A.name + " value: " + tag8A.stringValue())
    //Tag 8A value: Hello World
}
```


## Java

### Gradle
```
dependencies {
  implementation 'com.dowhilenotfalse:bertlv-jvm:1.0.0'
}
```

### Example
```
    public static void main(String[] args) throws TagException {
        BerTlv berTlv = new BerTlv();

        //Add Multiple Tags
        berTlv.addTags("8A01000C050101010101");

        //Update Tag
        Tag tag8A = berTlv.tag("8A");
        tag8A.setStringValue("Hello World");

        //Add Tag
        Tag newTag = new Tag("9F02", "0A1105");
        berTlv.addTag(newTag);

        //Delete Tag
        berTlv.deleteTag("0C");


        System.out.println("Tag List: " + berTlv.getTags()); 
        //Tag List: {8A=8A0B48656C6C6F20576F726C64, 9F02=9F02030A1105}        
        System.out.println("Tags: " + berTlv.toString()); 
        //Tags: 8A0B48656C6C6F20576F726C649F02030A1105
        System.out.println("Tag " + tag8A.getName() + ": " + tag8A.toString()); 
        //Tag 8A: 8A0B48656C6C6F20576F726C64
        System.out.println("Tag " + tag8A.getName() + " value: " + tag8A.stringValue());
        //Tag 8A value: Hello World
    }
```



## Version History

* 1.0.0
    * Initial Release

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
