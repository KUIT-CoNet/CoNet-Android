package com.kuit.conet.UI.Plan.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.R
import com.kuit.conet.databinding.DialogBottomSheetTimeBinding

class TimeDialog(var timeInfo: String): BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomSheetTimeBinding

    private var listener: BottomSheetListener? = null

    private var setHour: Int = 0
    private var setMinute: Int = 0
    interface BottomSheetListener {
        fun onAdditionalInfoSubmitted(info: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogBottomSheetTimeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timeTp.setOnTimeChangedListener { _, hourOfDay, minute ->
            setHour = hourOfDay
            setMinute = minute
        }

        binding.applyBtn.setOnClickListener{
            if(setHour == 0 && setMinute == 0){

            } else {
                if(setHour < 10 && setMinute < 10){
                    timeInfo = "0${setHour}:0${setMinute}"
                } else {
                    if(setHour <10){
                        timeInfo = "0${setHour}:${setMinute}"
                    } else if(setMinute < 10){
                        timeInfo = "${setHour}:0${setMinute}"
                    } else {
                        timeInfo = "${setHour}:${setMinute}"
                    }
                }
            }
            listener?.onAdditionalInfoSubmitted(timeInfo)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle( // Background -> Transparent.
            STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
}