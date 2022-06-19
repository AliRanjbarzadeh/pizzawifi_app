package ir.atriatech.extensions.dialog

import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import ir.atriatech.extensions.R
fun Fragment.filter(filter: Filter) {
    context?.let {
        var sort=0
        MaterialDialog(it)
            .customView(R.layout.dialog_fragment_filter)
            .show {

                cancelable(true)
                getCustomView().findViewById<AppCompatTextView>(R.id.txtOrderDistance)?.setOnClickListener {
                    sort=1
                    dismiss()

                }
                getCustomView().findViewById<AppCompatTextView>(R.id.txtOrderDate)?.setOnClickListener {
                    sort=2
                    dismiss()

                }
                getCustomView().findViewById<AppCompatTextView>(R.id.txtOrderFavourite)?.setOnClickListener {
                    sort=3
                    dismiss()

                }

                onDismiss {
                    filter.onFilter(sort)
                }
            }

    }}


interface Filter {
    fun onFilter(sortId:Int)
}