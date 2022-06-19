package ir.atriatech.extensions.kotlin

import java.text.NumberFormat
import java.util.*

fun Int.priceFormat(suffix: String = "تومان"): String {
	val format = NumberFormat.getNumberInstance(Locale.US).format(this)
	return if (suffix.isNotEmpty()) {
		("$format $suffix ")
	} else {
		format
	}
}