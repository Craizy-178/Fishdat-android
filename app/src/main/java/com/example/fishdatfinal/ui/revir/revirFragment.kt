package com.example.fishdatfinal.ui.revir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fishdat.data.AppData
import com.example.fishdatfinal.R
import com.example.fishdatfinal.databinding.FragmentRevirBinding

const val REVIR_NUMBER_SIZE = 6

class revirFragment : Fragment() {
    private var _binding: FragmentRevirBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRevirBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Nastavenie prvkov okna z AppData
        binding.etRevirNumber.setText(AppData.Data.RevirData.RevirNumber)
        binding.tvMessage.text = " "
        binding.btnStart.setOnClickListener { OnStart()}


        // Inflate the layout for this fragment
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun OnStart(){
        val revirNumber = binding.etRevirNumber.text.toString()
        if(revirNumber.isEmpty()){
            binding.tvMessage.text = "Číslo revíru musí byť zadané pred štartom lovu"
        }
        else if(revirNumber.length != REVIR_NUMBER_SIZE){
            binding.tvMessage.text = "Číslo revíru musí mať " + REVIR_NUMBER_SIZE.toString() + " znakov"
        }
        else {
            AppData.Data.RevirData.RevirNumber = revirNumber
            AppData.Data.WantStart=true
            AppData.Data.Activity.supportFragmentManager.popBackStack()
        }
    } //onstart





}