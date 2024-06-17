package com.example.fishdatfinal.ui.send

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.example.fishdat.data.AppData
import com.example.fishdatfinal.R
import com.example.fishdatfinal.databinding.FragmentSendBinding

class SendFragment : Fragment() {
    private var _binding: FragmentSendBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Nastavenie funkcii na stlacenie tlacitok
        binding.btnSend.setOnClickListener { OnSend() }
        binding.btnCancel.setOnClickListener { OnCancel() }
        //Ak je prvy krat spustene odosielanie, nastavim default SendData
        if(AppData.Data.SendData.Email.isEmpty()) {
            val settings = PreferenceManager.getDefaultSharedPreferences(AppData.Data.Activity)

            AppData.Data.SendData.Email = settings.getString("email", "").toString()
            AppData.Data.SendData.EmailSubject = settings.getString("email_subject", "").toString()
            AppData.Data.SendData.EmailText = settings.getString("email_text", "").toString()
            AppData.Data.SendData.DataFile = settings.getString("filename", "").toString()
        }
        //Nastavenie prvkov v UI
        binding.etEmail.setText(AppData.Data.SendData.Email)
        binding.etEmailSubject.setText(AppData.Data.SendData.EmailSubject)

        return root
    }
    private fun OnSend() {
        AppData.Data.SendData.Email = binding.etEmail.text.toString()
        AppData.Data.SendData.EmailSubject = binding.etEmailSubject.text.toString()

        AppData.Data.FishData.Save()
        AppData.Data.FishData.Send(AppData.Data.Activity)
    }


    private fun OnCancel() {
        // Navrat na predchadzajucu stranku - zoznam ulovkov = home page
        AppData.Data.Activity.supportFragmentManager.popBackStack()
    }

}