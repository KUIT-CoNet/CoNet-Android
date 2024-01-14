package com.kuit.conet.UI.Plan.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuit.conet.R
import com.kuit.conet.databinding.DialogBottomSheetEditTrashBinding

class EditTrashDialog : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: DialogBottomSheetEditTrashBinding? = null
    private val binding: DialogBottomSheetEditTrashBinding
        get() = requireNotNull(_binding) { "EditTrashDialog's binding is null" }
    private var _listener: OnDialogClickListener? = null
    private val listener: OnDialogClickListener
        get() = requireNotNull(_listener) { "EditTrashDialog's listener is null" }

    interface OnDialogClickListener {
        fun onEditButtonClick()
        fun onTrashButtonClick()
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this._listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DialogBottomSheetEditTrashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llEdit.setOnClickListener(this)
        binding.llTrash.setOnClickListener(this)
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
            R.id.ll_edit ->{
                listener.onEditButtonClick()
                dismiss()
            }
            R.id.ll_trash ->{
                listener.onTrashButtonClick()
                dismiss()
            }
        }
    }
}