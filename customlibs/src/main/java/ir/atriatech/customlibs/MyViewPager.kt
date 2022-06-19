package ir.atriatech.customlibs

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.viewpager.widget.ViewPager


class MyViewPager : ViewPager {

	private var isWrapContent: Boolean = false

	constructor(context: Context) : super(context)

	constructor(@NonNull context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyViewPager)

		try {
			isWrapContent = typedArray.getBoolean(R.styleable.MyViewPager_mViewPagerWrapHeight, false)
		} catch (e: Exception) {
		}

		typedArray.recycle()
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(ev: MotionEvent): Boolean {
		return false
	}

	override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
		return false
	}

//	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//		var mHeight = heightMeasureSpec
//		if (isWrapContent && childCount > 0) {
//			val mChild = getChildAt(0)
//			mChild.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
//			mHeight = mChild.measuredHeight
//		}
//		super.onMeasure(widthMeasureSpec, mHeight)
//	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		var heightMeasureSpec = heightMeasureSpec
		val mode = MeasureSpec.getMode(heightMeasureSpec)
		// Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
// At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
		if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) { // super has to be called in the beginning so the child views can be initialized.
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
			var height = 0
			for (i in 0 until childCount) {
				val child: View = getChildAt(i)
				child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
				val h: Int = child.getMeasuredHeight()
				if (h > height) height = h
			}
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
		}
		// super has to be called again so the new specs are treated as exact measurements
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
}