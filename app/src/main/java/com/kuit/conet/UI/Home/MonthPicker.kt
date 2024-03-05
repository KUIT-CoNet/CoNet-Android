package com.kuit.conet.UI.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.databinding.DialogMonthPickerBinding
import java.util.*

class MonthPicker : Fragment() {

    private var _binding: DialogMonthPickerBinding? = null
    private val binding: DialogMonthPickerBinding
        get() = requireNotNull(_binding) { "MonthPicker's binding is null" }
    private var _buttonClickListener: OnButtonClickListener? = null
    private val buttonClickListener: OnButtonClickListener
        get() = requireNotNull(_buttonClickListener) { "MonthPicker's listener is null" }

    interface OnButtonClickListener {
        fun onButtonClicked(year: Int, month: Int)
    }

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        _buttonClickListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMonthPickerBinding.inflate(inflater, container, false)
        Log.d(LIFECYCLE, "MonthPicker - onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "MonthPicker - onViewCreated() called")

        if (binding.tvYear.text.toString().isBlank()) {
            val year = Calendar.getInstance().get(Calendar.YEAR)
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

        listOf(
            binding.btnMonth1,
            binding.btnMonth2,
            binding.btnMonth3,
            binding.btnMonth4,
            binding.btnMonth5,
            binding.btnMonth6,
            binding.btnMonth7,
            binding.btnMonth8,
            binding.btnMonth9,
            binding.btnMonth10,
            binding.btnMonth11,
            binding.btnMonth12,
        ).forEachIndexed { index, button ->
            button.setOnClickListener {
                buttonClickListener.onButtonClicked(
                    year = binding.tvYear.text.toString().toInt(),
                    month = index + 1,
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        _buttonClickListener = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "MonthPicker - onDestroyView() called")
    }
}