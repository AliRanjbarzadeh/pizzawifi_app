package ir.atriatech.extensions.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import ir.atriatech.extensions.R
fun Fragment.twoState(twoState: TwoState, txtTitle:String, txtCancel:String="خیر", txtAccept:String="بله", isCancelable:Boolean=false) {
    context?.let {
        MaterialDialog(it)
            .customView(R.layout.dialog_fragment_two_state,noVerticalPadding = true,horizontalPadding = false)
            .show {
                noAutoDismiss()
                cancelable(isCancelable)
                cancelOnTouchOutside(isCancelable)
                getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.text=txtCancel
                getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.text=txtAccept
                getCustomView().findViewById<AppCompatTextView>(R.id.txtTitle)?.text=txtTitle
                getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.setOnClickListener {
                    dismiss()
                    twoState.onCanceled()
                }
                getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.setOnClickListener {
                    twoState.onConfirmed()
                    dismiss()

                }
            }
    }}

    fun AppCompatActivity.twoState(twoState: TwoState, txtTitle:String, txtCancel:String="خیر", txtAccept:String="بله", isCancelable:Boolean=true) {
            MaterialDialog(this)
                .customView(R.layout.dialog_fragment_two_state)
                .show {
                    noAutoDismiss()
                    cancelable(isCancelable)
                    cancelOnTouchOutside(isCancelable)
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtTitle)?.text=txtTitle
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.text=txtCancel
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.text=txtAccept
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.setOnClickListener {
                        dismiss()
                        twoState.onCanceled()
                    }
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.setOnClickListener {
                        dismiss()
                        twoState.onConfirmed()

                }
        }

}

interface TwoState {
    fun onConfirmed()
    fun onCanceled(){}
}