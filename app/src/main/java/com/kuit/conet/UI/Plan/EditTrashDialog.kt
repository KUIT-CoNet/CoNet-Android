package com.kuit.conet.UI.Plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.R
import com.kuit.conet.databinding.DialogBottomSheetEditTrashBinding

class EditTrashDialog : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogBottomSheetEditTrashBinding

    interface OnDialogClickListener {
        fun onEditButtonClick()
        fun onTrashButtonClick()
    }

    private var listener: OnDialogClickListener? = null

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogBottomSheetEditTrashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editLl.setOnClickListener(this)
        binding.trashLl.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle( // Background -> Transparent.
            STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.edit_ll ->{
                listener?.onEditButtonClick()
                dismiss()
            }
            R.id.trash_ll ->{
                listener?.onTrashButtonClick()
                dismiss()
            }
        }
    }
}