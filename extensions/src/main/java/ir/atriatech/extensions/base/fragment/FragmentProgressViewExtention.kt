package ir.atriatech.extensions.base.fragment

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.daimajia.easing.linear.Linear
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.extensions.R
import ir.atriatech.extensions.kotlin.d

fun BaseCoreFragment.setProgressView(
	viewGroup: ViewGroup,
	isLoading: Boolean? = null,
	type: Int = 1
) {

	isLoading?.let { loading ->

		if (loading) {
			val layoutInflater = LayoutInflater.from(requireContext())
			if (type == 1) {
				progressView = layoutInflater.inflate(R.layout.template_loading, viewGroup, false)
			} else if (type == 2) {
				progressView =
					layoutInflater.inflate(R.layout.template_loading_with_background, viewGroup, false)
			}

			progressView?.isClickable = true
			when (viewGroup) {
				is ConstraintLayout -> {
					val params = ConstraintLayout.LayoutParams(
						ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
						ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
					)
					params.topToTop = ConstraintSet.PARENT_ID
					params.bottomToBottom = ConstraintSet.PARENT_ID
					params.endToEnd = ConstraintSet.PARENT_ID
					params.startToStart = ConstraintSet.PARENT_ID
					progressView?.layoutParams = params
				}
				is CoordinatorLayout -> progressView?.let {
					val params = CoordinatorLayout.LayoutParams(
						CoordinatorLayout.LayoutParams.MATCH_PARENT,
						CoordinatorLayout.LayoutParams.MATCH_PARENT
					)
					params.behavior = AppBarLayout.ScrollingViewBehavior()
					it.layoutParams = params
				}
				is LinearLayout -> progressView?.let {
					it.minimumHeight = 0
					it.minimumWidth = 0

					val params = LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT
					)
					it.layoutParams = params
				}
				else -> progressView?.layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
				)
			}
			viewGroup.addView(
				progressView
			)

		} else {
			progressView?.let {
				Handler().postDelayed({
					viewGroup.removeView(it)
					isFirst = false
				}, 500)

			}


		}
	}
}
