package ir.atriatech.extensions.base.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.extensions.R

fun BaseCoreFragment.setEmptyView(emptyViewParent: ViewGroup, list: MutableList<Any>) {


    if (list.isEmpty()) {
        val layoutInflater = LayoutInflater.from(requireContext())
        emptyView = layoutInflater.inflate(R.layout.template_empty_view, null)
        emptyView?.isClickable = true
        when (emptyViewParent) {
            is ConstraintLayout -> {
                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                )
                params.topToTop = ConstraintSet.PARENT_ID
                params.bottomToBottom = ConstraintSet.PARENT_ID
                params.endToEnd = ConstraintSet.PARENT_ID
                params.startToStart = ConstraintSet.PARENT_ID
                emptyView?.layoutParams = params
            }
            is CoordinatorLayout -> emptyView?.let {
                val params = CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.MATCH_PARENT
                )
                params.behavior = AppBarLayout.ScrollingViewBehavior()
                it.layoutParams = params
            }
            else -> emptyView?.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        emptyViewParent.addView(
            emptyView
        )

    } else {
        emptyView?.let {
            {
                emptyViewParent.removeView(it)

            }


        }
    }
}
