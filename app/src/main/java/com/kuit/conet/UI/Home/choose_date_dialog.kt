package com.kuit.conet.UI.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.databinding.DialogDateChooseBinding
import java.util.*

class choose_date_dialog : Fragment(){

    lateinit var binding : DialogDateChooseBinding

    interface OnButtonClickListener {
        fun onButtonClicked(year : Int, month : Int)
    }

    private var buttonClickListener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDateChooseBinding.inflate(inflater, container, false)
        if(binding.tvYear.text.toString() == ""){
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            binding.tvYear.text = year.toString()
        }
        binding.ivPrevYear.setOnClickListener {
            var year = binding.tvYear.text.toString().toInt()
            year -= 1
            binding.tvYear.text = year.toString()
        }
        binding.ivNextYear.setOnClickListener {
            var year = binding.tvYear.text.toString().toInt()
            year += 1
            binding.tvYear.text = year.toString()
        }

        binding.btnMonth1.setOnClickListener {
            val month = 1
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth2.setOnClickListener {
            val month = 2
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth3.setOnClickListener {
            val month = 3
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }


        binding.btnMonth4.setOnClickListener {
            val month = 4
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth5.setOnClickListener {
            val month = 5
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth6.setOnClickListener {
            val month = 6
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }


        binding.btnMonth7.setOnClickListener {
            val month = 7
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth8.setOnClickListener {
            val month = 8
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)
        }

        binding.btnMonth9.setOnClickListener {
            val month = 9
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }



        binding.btnMonth10.setOnClickListener {
            val month = 10
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth11.setOnClickListener {
            val month = 11
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        binding.btnMonth12.setOnClickListener {
            val month = 12
            val year = binding.tvYear.text.toString().toInt()
            buttonClickListener?.onButtonClicked(year, month)

        }

        return binding.root
    }



}