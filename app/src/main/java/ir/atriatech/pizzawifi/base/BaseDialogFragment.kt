package ir.atriatech.pizzawifi.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import ir.atriatech.pizzawifi.R

open class BaseDialogFragment : DialogFragment() {

    protected var baseFragmentCallback: BaseFragmentCallback? = null

    @SuppressLint("ClickableViewAccessibility")
    protected fun initBase(
        progressBar: ProgressBar? = null,
        progressBarColor: Int = 0
    ) {
        if (context == null) {
            return
        }

        if (progressBarColor != 0)
            progressBar?.indeterminateDrawable?.setColorFilter(
                progressBarColor,
                PorterDuff.Mode.SRC_IN
            )
        else
            progressBar?.indeterminateDrawable?.setColorFilter(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorPrimary
                ), PorterDuff.Mode.SRC_IN
            )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BaseFragmentCallback)
            baseFragmentCallback = context
    }

    override fun onDetach() {
        super.onDetach()

        baseFragmentCallback = null
    }


}