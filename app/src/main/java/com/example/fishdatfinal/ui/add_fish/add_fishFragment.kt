package com.example.fishdatfinal.ui.add_fish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fishdat.data.AppData
import com.example.fishdat.data.FishDataItem
import com.example.fishdatfinal.R
import com.example.fishdatfinal.databinding.FragmentAddFishBinding


class add_fishFragment : Fragment() {
    private var _binding: FragmentAddFishBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFishBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Nastavenie funkcii na stlacenie tlacitok
        binding.btnSave.setOnClickListener { OnSave() }
        binding.btnCancel.setOnClickListener { OnCancel() }
        binding.btnEmpty.setOnClickListener{OnEmpty()}

        //Vymazanie chyboveho hlasenia
        binding.tvMessage.text = " "

        //Skontroluje sa zadanie reviru. Ak nie je reviz zadany, prepnem sa spat a vyzidam jeho nastavenie
        if(AppData.Data.RevirData.RevirNumber.isEmpty()) {
            AppData.Data.Activity.ShowRevir()
        }


        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun OnSave(){
        val fish: FishDataItem = FishDataItem()
        var isError:Boolean = false
        val fishLengthText = binding.etFishLength.text.toString()
        val fishWeightText = binding.etFishWeight.text.toString()

        fish.SetFishNameWithChesk(binding.etFishName.text.toString())
        if(fish.FishName.isEmpty()) {
            // Vypis chyboje hlasky pri nenastavenom nazve ryby
            binding.tvMessage.text = "Druhový názov ryby musí byť zadaný"
            isError = true
        }

        if(!isError) {
            if (fishLengthText.isEmpty()) {
                // Vypis chyboje hlasky pri nenastavenej velkosti ryby
                binding.tvMessage.text = "Veľkosť ryby musí byť zadaná"
                isError = true
            }
            else {
                fish.FishLength = fishLengthText.toInt()

                // Kontrola ci je zadana nenulova velkost ryby
                if(fish.FishLength <= 0) {
                    // Vypis chyboje hlasky pri zle zadanej velkosti
                    binding.tvMessage.text = "Veľkosť ryby musí byť vä%c%sia ako 0"
                    isError = true
                }
            }
        }

        if(!isError) {
            if (fishWeightText.isEmpty()) {
                // Vypis chyboje hlasky pri nenastavenej hmotnosti ryby
                binding.tvMessage.text = "Hmotnosť ryby musí byť zadaná"
                isError = true
            }
            else {
                fish.FishWeight = fishWeightText.toFloat()

                // Kontrola ci je zadana nenulova hmotnost ryby
                if(fish.FishWeight <= 0) {
                    // Vypis chyboje hlasky pri zle zadanej hmotnosti
                    binding.tvMessage.text = "Hmotnosť ryby musí byť vä%c%sia ako 0"
                    isError = true
                }
            }
        }

        //Ak je vseko OK, vlozi sa novy zaznam do zoznameu a vratime sa na zoznam ulovkov
        if(!isError) {
            // Nastavenie aktualneho reviru do ulovku a jeho ulozenie v zozname
            fish.RevirNumber = AppData.Data.RevirData.RevirNumber
            AppData.Data.FishData.Add(fish)
            AppData.Data.Activity.supportFragmentManager.popBackStack()
        }

    }
    private fun OnCancel(){
        // Navrat na predchadzajucu stranku - zoznam ulovkov = home page
        AppData.Data.Activity.supportFragmentManager.popBackStack()
    }
    private fun OnEmpty(){
        val emptyfish: FishDataItem = FishDataItem()
        emptyfish.SetEmpty()
        emptyfish.RevirNumber = AppData.Data.RevirData.RevirNumber
        AppData.Data.FishData.Add(emptyfish)
        AppData.Data.Activity.supportFragmentManager.popBackStack()

    }


}