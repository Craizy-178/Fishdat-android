package com.example.fishdat.data

import android.os.Environment
import androidx.preference.PreferenceManager
import com.example.fishdatfinal.MainActivity
import java.io.File


//Hlavna trieda pre spravu udajov v aplikacii.
// je implementovana ako staticka => aobjekt tejto tiredy sa vytvori automaticky pri prvom pouziti triedy a je pristupny vsade z aplikacie
class AppData {
    // Trieda AppData si drzi aktualny zoznam ulovkov v triede FishDataList
    val FishData:FishDataList = FishDataList()
    val RevirData:RevirData = RevirData()
    val SendData:SendData = SendData()
    lateinit var Activity: MainActivity
    var WantStart:Boolean = false
    val FISHDAT_FOLDER:String = "FishDat"
    val FISHDAT_DEFAULT_DATAFILE:String = "data"

    // Privatna premenna, ktory sleduje, ci sa jedna o prvu inicializaciu triedy - pouziva sa vo funkcii Initialize
    private var _isInitialized:Boolean = false

    // Toto zabezpeci staticku triedu - vytvorenie premmenej Data typu AppData
    // Trieda vytvori sama seba,
    // Moze to byt pouzite vsade tam, kde v ramci jednej aplikacie potrebujes len jednu instanciu/objekt danej triedy a chce ho mat pristupny vsade v aplikacii
    // K objektu prsupujes cez nazov triedy a premennu Data napr. AppData.Data.Initialize() alebo AppData.Data.FishData.Save() alebo AppData.Data.FishData.Items
    companion object {
        val Data = AppData()
    }

    // Funkcia na prvu inicializaciu dat pre aplikaciu
    fun Initialize(){
        // Data sa inicializuju len pri prvom volani funkcie _isInitialized == false
        if(!_isInitialized ) {

            // Nastavenie ídajov zo settings
            val settings = PreferenceManager.getDefaultSharedPreferences(AppData.Data.Activity)

            AppData.Data.SendData.Email = settings.getString("email", "").toString()
            AppData.Data.SendData.EmailSubject = settings.getString("email_subject", "-").toString()
            AppData.Data.SendData.EmailText = settings.getString("email_text", "").toString()
            AppData.Data.SendData.DataFile = settings.getString("filename", FISHDAT_DEFAULT_DATAFILE).toString()

            try {
                //Kontrola existencie adresara
                //val path = Environment.getExternalStorageDirectory()


                val path = AppData.Data.Activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val dataDirectory = File(path, FISHDAT_FOLDER)
                if (!dataDirectory.exists()) {
                    dataDirectory.mkdirs()
                }
            }
            catch(e:Exception){
                var str = e.toString()
            }

            //Nacitanie posledneho stavu dat vz CSV suboru
            FishData.Load()

            //nastavenie revir number na posledny nacitany zo suboru
            if(FishData.GetItemCount() > 0)
                RevirData.RevirNumber = FishData.Items[FishData.GetItemCount() - 1].RevirNumber

            // Poznaci si, ze uz je nainicializovana. Pri opakovanom volani uz data necita, neprepise si aktualne data
            _isInitialized = true
        }
    }
}