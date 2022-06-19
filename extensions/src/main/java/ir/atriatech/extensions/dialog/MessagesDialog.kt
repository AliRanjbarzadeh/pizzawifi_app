package ir.atriatech.extensions.dialog

import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import ir.atriatech.extensions.R

fun Fragment.messageDialog(txtContent: String = "", txtTitle: String = "") {
    context?.let {
        MaterialDialog(it)
            .customView(
                R.layout.dialog_fragment_messages,
                scrollable = false,
                noVerticalPadding = true
            )
            .show {
                cancelable(true)
                cancelOnTouchOutside(true)
                getCustomView().findViewById<AppCompatTextView>(R.id.txtTitle).text = txtTitle
                getCustomView().findViewById<AppCompatTextView>(R.id.txtContent).text = txtContent
//                getCustomView().findViewById<AppCompatTextView>(R.id.txtContent)?.movementMethod= ScrollingMovementMethod()
                getCustomView().findViewById<AppCompatTextView>(R.id.txtClose).setOnClickListener {

                    dismiss()

                }
            }
    }
}