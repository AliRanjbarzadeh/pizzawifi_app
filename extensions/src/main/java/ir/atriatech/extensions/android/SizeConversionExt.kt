package ir.atriatech.extensions.android

import android.content.res.Resources
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP

private val metrics = Resources.getSystem().displayMetrics


val Float.dp: Float      // [xxhdpi](360 -> 1080)
	get() = TypedValue.applyDimension(COMPLEX_UNIT_DIP, this, metrics)

val Int.dp: Int      // [xxhdpi](360 -> 1080)
	get() = TypedValue.applyDimension(COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()

val Float.sp: Float      // [xxhdpi](360 -> 1080)
	get() = TypedValue.applyDimension(COMPLEX_UNIT_SP, this, metrics)

val Int.sp: Int      // [xxhdpi](360 -> 1080)
	get() = TypedValue.applyDimension(COMPLEX_UNIT_SP, this.toFloat(), metrics).toInt()

val Number.px: Number      // [xxhdpi](360 -> 360)
	get() = this

val Number.px2dp: Int       // [xxhdpi](360 -> 120)
	get() = (this.toFloat() / metrics.density).toInt()

val Number.px2sp: Int       // [xxhdpi](360 -> 120)
	get() = (this.toFloat() / metrics.scaledDensity).toInt()