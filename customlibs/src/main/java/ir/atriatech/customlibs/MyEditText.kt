package ir.atriatech.customlibs

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class MyEditText : TextInputEditText {
    var mOriginalLeftPadding = -1f

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}


    interface OnCutCopyPasteListener {
        fun onCut()

        fun onCopy()

        fun onPaste()
    }

    var onCutCopyPasteListener: OnCutCopyPasteListener? = null


    override fun onTextContextMenuItem(id: Int): Boolean {
        val consumed = super.onTextContextMenuItem(id)

        when (id) {
            android.R.id.cut -> {
                onCutCopyPasteListener?.onCut()
            }

            android.R.id.copy -> {
                onCutCopyPasteListener?.onCopy()
            }

            android.R.id.paste -> {
                onCutCopyPasteListener?.onPaste()
            }
        }

        return consumed
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculatePrefix()
    }

    private fun calculatePrefix() {
        if (mOriginalLeftPadding == -1f) {
            if (tag != null) {
                val prefix = tag as String
                val widths = FloatArray(prefix.length)
                paint.getTextWidths(prefix, widths)
                var textWidth = 0f
                for (w in widths) {
                    textWidth += w
                }
                mOriginalLeftPadding = compoundPaddingLeft.toFloat()
                setPadding(
                    (textWidth + mOriginalLeftPadding).toInt(),
                    paddingRight, paddingTop,
                    paddingBottom
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (tag != null) {
            val prefix = tag as String
            canvas?.drawText(
                prefix, mOriginalLeftPadding,
                getLineBounds(0, null).toFloat(), paint
            )
        }
    }
}