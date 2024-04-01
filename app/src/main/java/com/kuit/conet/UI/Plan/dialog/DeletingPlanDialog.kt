package com.kuit.conet.UI.Plan.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuit.conet.R
import com.kuit.conet.Utils.LIFECYCLE
import com.kuit.conet.databinding.DialogDeletingPlanBinding

class DeletingPlanDialog : DialogFragment() {

    private var _binding: DialogDeletingPlanBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "DeletingPlanDialog's binding is null" }
    private var _listener: DialogClickListener? = null
    private val listener
        get() = requireNotNull(_listener) { "DeletingPlanDialog's listener is null" }

    interface DialogClickListener {
        fun onDeleteButtonClick()
    }

    fun setListener(listener: DialogClickListener) {
        this._listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "DeletingPlanDialog - onCreate() called")
        setStyle( // Background -> Transparent.
            STYLE_NORMAL,
            R.style.TransparentBottomSheetDialogFragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(LIFECYCLE, "DeletingPlanDialog - onCreateView() called")
        _binding = DialogDeletingPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LIFECYCLE, "DeletingPlanDialog - onViewCreated() called")

        binding.tvDeleteDialogCancel.setOnClickListener {
            dismiss()
        }

        binding.tvDeleteDialogDelete.setOnClickListener {
            listener.onDeleteButtonClick()
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Log.d(LIFECYCLE, "DeletingPlanDialog - onDestroyView() called")
    }

    companion object {
        const val TAG = "DeletingPlanDialog"
    }
}