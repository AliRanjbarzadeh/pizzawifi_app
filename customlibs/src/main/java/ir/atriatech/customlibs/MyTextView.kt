package ir.atriatech.customlibs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import ir.atriatech.extensions.android.dp2px

class MyTextView : AppCompatTextView {

	val mPaint = Paint().apply {
		color = Color.BLACK
	}

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTextView)

		try {
			val mPaintColor = typedArray.getColor(R.styleable.MyTextView_mLineColor, Color.BLACK)
			mPaint.color = mPaintColor

//			val mPaintHeight = typedArray.getDimensionPixelSize(R.styleable.MyTextView_mLineHeight, dp2px(2))
//			mPaint.strokeWidth = mPaintHeight.toFloat()
		} catch (e: Exception) {
		}

		typedArray.recycle()
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		canvas?.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), mPaint)
	}
}