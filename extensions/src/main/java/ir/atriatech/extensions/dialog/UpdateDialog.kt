package ir.atriatech.extensions.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import ir.atriatech.core.entities.Update
import ir.atriatech.extensions.R
import ir.atriatech.extensions.android.finishAffinity
import ir.atriatech.extensions.android.startAction

fun Fragment.update(update: Update, txtCancel: String = "بستن", txtAccept: String = "دریافت آپدیت") {
    if (update.updateType == 0) {

    } else {
        val isCancelable: Boolean = update.updateType == 1
        context?.let {
            MaterialDialog(it)
                .customView(R.layout.dialog_fragment_two_state,noVerticalPadding = true)
                .show {
                    noAutoDismiss()
                    cancelable(isCancelable)
                    cancelOnTouchOutside(isCancelable)
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.text = txtCancel
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.text = txtAccept
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtTitle)?.text = update.updateMessage
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.setOnClickListener {
                        dismiss()
                        if (!isCancelable){
                            finishAffinity()
                        }

                    }
                    getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.setOnClickListener {
                        startAction(update.updateLink)
                        dismiss()

                    }
                }
        }
    }
}

fun AppCompatActivity.update(update: Update, txtCancel: String = "بستن", txtAccept: String = "دریافت آپدیت") {
    if (update.updateType == 0) {

    } else {
        val isCancelable: Boolean = update.updateType == 1

        MaterialDialog(this)
            .customView(R.layout.dialog_fragment_two_state)
            .show {
                noAutoDismiss()
                cancelable(isCancelable)
                cancelOnTouchOutside(isCancelable)
                getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.text = txtCancel
                getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.text = txtAccept
                getCustomView().findViewById<AppCompatTextView>(R.id.txtTitle)?.text = update.updateMessage
                getCustomView().findViewById<AppCompatTextView>(R.id.txtNo)?.setOnClickListener {
                    dismiss()
                    finishAffinity()

                }
                getCustomView().findViewById<AppCompatTextView>(R.id.txtYes)?.setOnClickListener {
                    startAction(update.updateLink)
                    dismiss()

                }

            }
    }
}