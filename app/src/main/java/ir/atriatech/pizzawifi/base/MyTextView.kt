package ir.atriatech.pizzawifi.base

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.google.android.material.textview.MaterialTextView
import ir.atriatech.pizzawifi.R

class MyTextView : MaterialTextView {

	private var badgeWidth: Int = 0
	private var badgePadding: Int = 0
	private var badgeColor: Int = 0
	private var badgeTextSize: Int = 0
	private var badgeTextColor: Int = 0
	private var textWidth: Float = 0f
	private lateinit var badgeTypeface: Typeface
	private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
	private val circlePaint = Paint()

	private var _badgeCount: Int = 0
	var badgeCount: Int
		get() = _badgeCount
		set(value) {
			_badgeCount = value
			invalidate()
			requestLayout()
		}

	constructor(context: Context) : super(context) {
		init(null)
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init(attrs)
	}

	private fun init(@Nullable attrs: AttributeSet?) {
		if (attrs == null)
			return

		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTextView)

		badgeWidth = typedArray.getDimensionPixelSize(R.styleable.MyTextView_badgeWidth, 0)
		badgePadding = typedArray.getDimensionPixelSize(R.styleable.MyTextView_badgePadding, 0)
		_badgeCount = typedArray.getInt(R.styleable.MyTextView_badgeCount, 0)
		badgeColor = typedArray.getColor(R.styleable.MyTextView_badgeColor, Color.RED)
		badgeTextSize = typedArray.getDimensionPixelSize(R.styleable.MyTextView_badgeTextSize, paint.textSize.toInt())
		badgeTextColor = typedArray.getColor(R.styleable.MyTextView_badgeTextColor, Color.WHITE)
		val badgeFont = typedArray.getString(R.styleable.MyTextView_badgeFont)

		if (badgeFont != null)
			badgeTypeface = Typeface.createFromAsset(context.assets, badgeFont)
		else
			badgeTypeface = typeface

		typedArray.recycle()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		calculateBadge()
	}

	private fun calculateBadge() {
		val bounds = Rect()
		paint.getTextBounds(text.toString(), 0, text.toString().length, bounds)
		textWidth = bounds.width().toFloat()

		var mPaddingLeft = badgeWidth
		if (textWidth > width - badgeWidth)
			mPaddingLeft += (6 * context.resources.displayMetrics.density).toInt()

		setPadding(mPaddingLeft, paddingRight, paddingTop, paddingBottom)
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		if (_badgeCount > 0) {
			//Circle
			var centerX = width - textWidth - badgePadding - badgeWidth * 0.5f
			val centerY = height * 0.55f

			if (textWidth > width - badgeWidth)
				centerX = (badgeWidth - badgePadding).toFloat()

			circlePaint.color = badgeColor
			circlePaint.flags = Paint.ANTI_ALIAS_FLAG

			canvas?.drawCircle(centerX, centerY, badgeWidth * 0.5f, circlePaint)

			//Number
			textPaint.textSize = badgeTextSize.toFloat()
			textPaint.color = badgeTextColor
			textPaint.typeface = badgeTypeface
			var badgeText = "" + _badgeCount
			if (_badgeCount > 99)
				badgeText = "+99"

			val textOffsetX = textPaint.measureText(badgeText) * -0.5f
			val textOffestY = textPaint.getFontMetrics().ascent * -0.3f

			canvas?.drawText(badgeText, centerX + textOffsetX, centerY + textOffestY, textPaint)
		}
	}
}
