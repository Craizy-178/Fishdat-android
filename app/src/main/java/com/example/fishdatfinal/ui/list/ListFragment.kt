package com.example.fishdatfinal.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fishdat.data.AppData
import com.example.fishdatfinal.R
import com.example.fishdatfinal.databinding.FragmentListBinding


class ListFragment : Fragment() {
    private val _fishDataListAdapter = FishDataListAdapter()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Nastavenie akcie pre rychle tlacidlo actionAdd - pridanie noveho zaznamu
        binding.actionAdd.setOnClickListener { DoAddFish() }

        // Nastavenie akcie pre vymazanie vybranych zaznamov actionDelete
        binding.actionDelete.setOnClickListener { AppData.Data.FishData.DeleteSelected() }

        //prepojenie UI zoznamu prvkov s datami cez FishDataListAdapter
        val list = binding.rvList
        list.adapter = _fishDataListAdapter
        list.layoutManager = LinearLayoutManager(getContext())
        //nastavenie akcie na check/uncheck prvku v zozname
        _fishDataListAdapter.onItemChecked = { CheckItemSelection() }
        _fishDataListAdapter.onListModified = { CheckItemSelection() }

        //nastavenie cisla reviru pri prepnuti na fragment
        var revirNumber = binding.tvRevirNumber
        if(AppData.Data.RevirData.RevirNumber.isNotEmpty()){
            revirNumber.text = AppData.Data.RevirData.RevirNumber
        }
        else {
            revirNumber.setText("Vyber číslo revíru, kde lovíš")
        }

        // Skontrolujem si stav vyberu prvkov v zozname aby sa zobrazil/zhasol button actionDelete
        CheckItemSelection()

        // Ak sa ma spustit lov - bol nastaveny revir prepnem sa na ADDFish Fragment
        if(AppData.Data.WantStart){
            AppData.Data.WantStart = false
           AppData.Data.Activity.ShowAddFish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Funkcia sa volá pri stlaćení actionSend
    private fun DoAddFish(){
        //Najskor skontrolovat ci je zadany revir
        if(AppData.Data.RevirData.RevirNumber.isEmpty()) {

            // Revir nie je zadany, treba ho nastavit
            AppData.Data.Activity.ShowRevir()
        }
        else {
            // Revir je zadany, mozem lovit
            AppData.Data.Activity.ShowAddFish()
        }
    }
    private fun CheckItemSelection() {
        if (AppData.Data.FishData.GetItemCount()>0 && AppData.Data.FishData.IsAnySelected()) {
            binding.actionDelete.setVisibility(View.VISIBLE)
        } else {
            binding.actionDelete.setVisibility(View.INVISIBLE)
        }

    }


}