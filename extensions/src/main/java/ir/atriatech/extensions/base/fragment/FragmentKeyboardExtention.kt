package ir.atriatech.extensions.base.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.extensions.R
import ir.atriatech.extensions.android.children
import ir.atriatech.extensions.android.hideInputMethod
import ir.atriatech.extensions.kotlin.d
import java.util.concurrent.TimeUnit

@SuppressLint("ClickableViewAccessibility")
fun BaseCoreFragment.hideKeyboard(mainView: View) {
    mainView.setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    bottomSheetBehavior?.let {
                        it.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    hideInputMethod()
                    return false
                }
            }
            return false
        }

    })

}

@SuppressLint("ClickableViewAccessibility")
fun BaseCoreFragment.showDisableBack(viewGroup: ViewGroup) {
    if (!disableViewIsShown) {
        disableViewIsShown = true
        val layoutInflater = LayoutInflater.from(requireContext())
        disableView = layoutInflater.inflate(R.layout.template_disable_back, null)
        disableView?.isClickable = true
        disableView?.isFocusable = true
        disableView?.requestFocus()
        for (i in viewGroup.children) {
            i.clearFocus()
        }
        if (viewGroup is ConstraintLayout) {
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            params.topToTop = ConstraintSet.PARENT_ID
            params.bottomToBottom = ConstraintSet.PARENT_ID
            params.endToEnd = ConstraintSet.PARENT_ID
            params.startToStart = ConstraintSet.PARENT_ID
            disableView?.layoutParams = params
        } else {
            disableView?.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        viewGroup.addView(disableView)
        disableView?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        bottomSheetBehavior?.let {
                            it.state = BottomSheetBehavior.STATE_HIDDEN
                            hideDisableBack(viewGroup)

                        }
                        return false
                    }
                }
                return false
            }

        }
        )

    }

}

fun BaseCoreFragment.hideDisableBack(viewGroup: ViewGroup) {
    if (disableViewIsShown) {
        disableViewIsShown = false
        disableView?.let {
            viewGroup.removeView(it)
        }
    }
}