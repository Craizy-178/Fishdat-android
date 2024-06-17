package com.example.fishdat.data

// Trieda obsahuje informacie o jednom ulovku = rybe
class FishDataItem {
    // Zoznam premennych - informácií o úlovku
    var RevirNumber:String = ""   //Cislo / Kod reviru
    var FishName:String = ""     // Typ/nazov ryby
    var FishLength:Int = 0   // velkost/dlzka ryby v cm
    var FishWeight:Float = 0f   // Hmotnost ryby v kg

    var IsSelected:Boolean = false   // prvok v zozname ulovkov je vybrany

    fun toCSVString():String {
        var result:String = ""
        result += RevirNumber + ";"
        result += FishName + ";"
        if(IsEmpty()){
            result += "-;-"
        }
        else {
            result += FishLength.toString() + ";"
            result += FishWeight.toString()
        }

        return result
    }
    fun fromString(text:String): Boolean {
        var result: Boolean = false
        val data = text.split(";")

        if (data.size == 4) {
            RevirNumber = data[0]
            FishName = data[1]
            if(data[2] == "-"){
                FishLength = 0
            }
            else {
                FishLength = data[2].toInt()
            }
            if(data[3] == "-"){
                FishWeight = 0f
            }
            else {
                FishWeight = data[3].toFloat()
            }

            result = true
        }
        return result
    }
    fun SetFishNameWithChesk(fishName:String) {
        FishName = fishName.replace(';', '_')
        FishName = FishName.replace('\n', '_')
    }
    fun SetEmpty(){
        FishName = "-"
        FishLength = 0
        FishWeight = 0f
    }
    fun IsEmpty():Boolean{
        return (FishName == "-" && FishLength == 0 && FishWeight == 0f)
    }

}

