package com.example.fishdat.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.OutputStream


// Trieda so zoznamom ulovokv
class FishDataList {
    //Event na notifikaciu, ze sa zmenil list
    var onListModified: (() -> Unit)? = null
    var onItemAdded: (() -> Unit)? = null

    //Clemnske premenne triedy - udrzuju data pre triedu DishDataList
    val Items:MutableList<FishDataItem> = mutableListOf() //Zoznam ulovkov MutableList je vseobecna trieda, ktora udrzuje zoznam prvkov jedneho typu typ musi byt definavany v zatvorkach <>
    var DataFilePath:String = "" // cesta k datovemu suboru CSV

    //Funkcia prida jeden rpvok/ulovok do zoznamu
    public fun Add(item:FishDataItem){
        Items.add(item)
        Save() //Ulozenie aktualneho zoznamu
        onItemAdded?.invoke()
    }

    //Funkcia vymaze vybrane prvky zo zoznamu
    fun DeleteSelected() {
        Items.removeAll { i -> i.IsSelected}
        Save() //Ulozenie aktualneho zoznamu
        onListModified?.invoke()
    }

    fun Clear() {
        Items.clear()
        Save() //Ulozenie aktualneho zoznamu
        onListModified?.invoke()
    }

    //Funkcia ulozi aktualny zoznam prvkov do suboru s cestou fielpath. Ak filepath nie je zadana, zoberie svoju aktualnu DataFilePath
    public fun Save(filepath:String = "")
    {
        // Ak je filePath zadana, nastavi si premennu DataFilePath pre neskorsie pouzitie
        if(filepath.isNotEmpty()){
            DataFilePath = filepath
        }
        else if(DataFilePath.isEmpty()){

            DataFilePath = AppData.Data.SendData.DataFile
        }

        //Ak nie je nastavene meno suboru, nastavi sa na default
        if(DataFilePath.isEmpty()) DataFilePath = AppData.Data.FISHDAT_DEFAULT_DATAFILE


        try {
            //Ziska cestu do externeho storage
            //val path = Environment.getExternalStorageDirectory()
            val path = AppData.Data.Activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val dataDirectory = File(
                path,
                AppData.Data.FISHDAT_FOLDER
            ) // Hlavny datovy folder pre FishDat aplikaciu
            val file = File(dataDirectory, DataFilePath) //Nastavenie cesty k datovemu suboru

            //Vymazanie obsahu suboru - zapis prazdneho textu
            file.writeText("\n")

            //Pre vsetky prvku v zozname vygeneruje jeden riadok - text a zapise do suboru
            for (item in Items) {
                file.appendText(item.toCSVString() + "\n")  //Kazdy riadok musi byt ukonceny znakom \n
            }
        }
        catch(e:Exception){
            var str = e.toString()
        }
    }


    //Funkcia vrati true ak je niektory z prvkov vybrany = isSelected = true
    public fun IsAnySelected():Boolean {
        return Items.any { it.IsSelected }
    }

    //Funkcia macita aktualny zoznam prvkov do suboru s cestou fielpath. Ak filepath nie je zadana, zoberie svoju aktualnu DataFilePath
    public fun Load(filepath:String = "") {
        // Ak je filePath zadana, nastavi si premennu DataFilePath pre neskorsie pouzitie
        if(filepath.isNotEmpty()){
            DataFilePath = filepath
        }
        else if(DataFilePath.isEmpty()){

            DataFilePath = AppData.Data.SendData.DataFile
        }

        //Ak nie je nastavene meno suboru, nastavi sa na default
        if(DataFilePath.isEmpty()) DataFilePath = AppData.Data.FISHDAT_DEFAULT_DATAFILE

        try {
            //Ziska cestu do externeho storage
            //val path = Environment.getExternalStorageDirectory()
            val path = AppData.Data.Activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val dataDirectory = File(
                path,
                AppData.Data.FISHDAT_FOLDER
            ) // Nastavenie hlavneho datoveho foldra pre aplikaciu FishDat
            val file = File(dataDirectory, DataFilePath) // Nastavenie datoveho suboru

            //Vymazanie aktualneho zoznamu prvkov
            Items.clear()

            // Ak subor existuje spusti sa nacitavanie dat
            if (file.exists()) {
                var lines = file.readLines() //Nacitanie obsahu

                // Pre vsetky riadky v zozname riadkov vygeneruje novy prvok
                for (line in lines) {
                    val item: FishDataItem = FishDataItem()

                    if (item.fromString(line)) Items.add(item) //Ak sa podari konverzia textoveho riadku, prida sa prvok do zoznamu
                }
            }
        }
        catch(e:Exception){
            var str = e.toString()
        }

    }


    // Funkcia vrati aktualny pocet prvkov v zozname
    public fun GetItemCount():Int {
        return Items.size
    }

    //Funkcia vygeneruje email a spusti default aplikaciu na odosielanie mailov.
    public fun Send(main: AppCompatActivity) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.setType("text/csv")
        //intent.setData(Uri.parse("mailto:"))
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(AppData.Data.SendData.Email))
        intent.putExtra(Intent.EXTRA_SUBJECT, AppData.Data.SendData.EmailSubject)
        intent.putExtra(Intent.EXTRA_TEXT, AppData.Data.SendData.EmailText)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val uri = ShareDataFile()
        if(uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri)
        }

        main.startActivity(Intent.createChooser( intent , "Vyber Email klient aplikáciu"))
    }

    fun ShareDataFile():Uri? {
        var result: Uri? = null
        val resolver: ContentResolver? =
            AppData.Data.Activity.contentResolver // resolver zabezpecukje pristup aplikacie k zdrojom napr. suborom
        var filename = AppData.Data.SendData.DataFile  //Nazov datoveho suboru
        filename = filename.replace(
            '.',
            '_'
        )  //nazov suboru nemoze obsahovat priponu napr. .csv. Priponu prida suboru automaticky sluzba systemu podla jeho tpyu

        //Nastavenie paramterov pre vytvorenie noveho suboru
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename.toString())   // Nazov suboru
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        // Zistenie ci subor uz v adresari documents existuje
        val uri: Uri? = MediaStore.Files.getContentUri("external") // Cesta na storage/Documents
        var fileUri: Uri? = null // csta k cielovemu suboru

        try {
            if (uri != null) {
                // Ziskanie zoznamu suborov v adresari Documents
                val cursor: Cursor? = resolver?.query(uri, null, null, null)

                //K nazvu suboru pridam priponu, aby som mal uplny nazov na disku
                filename = filename + ".csv"

                // Ak bol najdeny nejaky subor, pozriem, ci je to moj
                if (cursor != null && cursor.count > 0) {
                    // prejdem vsetky subory, kym nenajdem moj, alebo skontrolujem vsetky
                    while (cursor.moveToNext()) {
                        // Index do zoznamu dat s informaciami o suboroch. Udaje su v poli a poradie a mnozstvo udajov sa moze menit. Preto je potrebne si vyziadat index do pola podla nazvu hladaneho udaju
                        val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        if (nameIndex > -1) { // Hladany udaj je k dispozicii
                            //nazov spracovavaneho suboru zo zoznamu
                            val displayName = cursor.getString(nameIndex)
                            // Test, ci meno suboru je moje pozadovane
                            if (displayName == filename) {
                                //Ak sa jedna o moj subor, vypytam si jeho cislo a vytvrim cestu k tomuto suboru
                                //V Uri nie je nazov suboru ale jeho unikatne cislo, s ktorym sa subor v systeme identifikuje
                                val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                                if (idIndex > -1) {
                                    // Cislo suboru
                                    val id = cursor.getLong(idIndex)
                                    //Cesta k suboru
                                    fileUri = ContentUris.withAppendedId(uri, id)
                                }

                                break //Subor sa nasiel, dalej uz adresar neriesim
                            }
                        }
                    }

                    // Uvolnenie pohladu na zoznam suborov
                    cursor.close()
                }

                //Ak sa subor nenasiel, vygenerujem cestu pre novy subor
                if (fileUri == null) {
                    fileUri = resolver?.insert(uri, contentValues)
                }

                //otvorenie a nacitanie lokalnej kopie datoveho suboru
                val path =
                    AppData.Data.Activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val dataDirectory = File(
                    path,
                    AppData.Data.FISHDAT_FOLDER
                ) // Nastavenie hlavneho datoveho foldra pre aplikaciu FishDat
                val file = File(
                    dataDirectory,
                    AppData.Data.SendData.DataFile
                ) // Nastavenie datoveho suboru
                val uriPath = Uri.fromFile(file)
                var data = file.readBytes() //nacitanie suboru

                //Vytvorenie noveho suboru a nakopirovanie
                var outputStream: OutputStream? = resolver?.openOutputStream(fileUri!!, "wt")
                outputStream?.write(data)
                outputStream?.close()

                result = fileUri
            }
        }
        catch(e:Exception) {
            var err = e.toString()
        }

        return result;
    }

}
