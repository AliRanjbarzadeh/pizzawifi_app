package ir.atriatech.extensions.app

import android.graphics.Typeface
import ir.atriatech.extensions.R

fun makeTypeFace(fontPath: String = findString(R.string.app_font)): Typeface {
	return Typeface.createFromAsset(app.assets, fontPath)
}