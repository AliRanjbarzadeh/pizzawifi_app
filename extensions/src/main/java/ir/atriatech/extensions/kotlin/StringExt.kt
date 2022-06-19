package ir.atriatech.extensions.kotlin

import android.graphics.*
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ir.atriatech.core.BuildConfig
import ir.atriatech.core.util.custom_class.CustomTypefaceSpan
import ir.atriatech.extensions.app.app
import ir.atriatech.extensions.app.makeTypeFace
import java.security.MessageDigest
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.toast(isShortToast: Boolean = true) =
	ir.atriatech.extensions.android.toast(this, isShortToast)

fun String.md5() = encrypt(this, "MD5")

fun String.sha1() = encrypt(this, "SHA-1")

fun String.isIdcard(): Boolean {
	val p18 =
		"^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]\$".toRegex()
	val p15 = "^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]\$".toRegex()
	return matches(p18) || matches(p15)
}

fun String.isPhoneInAllCountries(): Boolean {
	val p = "^1([34578])\\d{9}\$".toRegex()
	return matches(p)
}

fun String.isEmail(): Boolean {
	val p = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$".toRegex()
	return matches(p)
}

fun isEmailValidate( email :String) : Boolean
{
	val pattern: Pattern
	val matcher: Matcher
	val EMAIL_PATTERN: String  = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	pattern = Pattern.compile(EMAIL_PATTERN)
	matcher = pattern.matcher(email)
	return matcher.matches()
}


fun String.isNumeric(): Boolean {
	val p = "^[0-9]+$".toRegex()
	return matches(p)
}

fun String.equalsIgnoreCase(other: String) = this.toLowerCase().contentEquals(other.toLowerCase())

private fun encrypt(string: String?, type: String): String {
	val bytes = MessageDigest.getInstance(type).digest(string!!.toByteArray())
	return bytes2Hex(bytes)
}

internal fun bytes2Hex(bts: ByteArray): String {
	var des = ""
	var tmp: String
	for (i in bts.indices) {
		tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
		if (tmp.length == 1) {
			des += "0"
		}
		des += tmp
	}
	return des
}

fun String.toPersian(): String {
	return this
}

fun String.toEnglish(): String {
	val chars = CharArray(length)
	for (i in 0 until length) {
		var ch: Char = get(i)
		if (ch.toInt() in 0x0660..0x0669) {
			ch -= 0x0660.toChar() - '0'.toInt().toChar()
		} else if (ch.toInt() in 0x06f0..0x06F9) {
			ch -= 0x06f0.toChar() - '0'.toInt().toChar()
		}
		chars[i] = ch
	}
	return String(chars)
}

fun String.isMobile(): Boolean = this.matches("09\\d{9}".toRegex())
fun String.isFixPhoneNumber(): Boolean = this.matches("0\\d{10}".toRegex())

fun String.getImageSize(density: Float, size: String = "", isCustomSize: Boolean = false): Uri {
	if (isCustomSize) {
		val lastDot = this.lastIndexOf(".")
		var imgAddress = this.subSequence(0, lastDot)
		val suffix = this.subSequence(lastDot, this.lastIndex + 1)
		imgAddress = imgAddress.toString() + size + suffix
		return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
			Uri.parse(BuildConfig.UPLOAD_URL_UNSAFE + imgAddress)
		} else {
			Uri.parse(BuildConfig.UPLOAD_URL + imgAddress)
		}
	}
	when {
		density < 2f -> return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
			Uri.parse(BuildConfig.UPLOAD_URL_UNSAFE + this)
		} else {
			Uri.parse(BuildConfig.UPLOAD_URL + this)
		}
		density in 2f..3f -> {
			val lastDot = this.lastIndexOf(".")
			var imgAddress = this.subSequence(0, lastDot)
			val suffix = this.subSequence(lastDot, this.lastIndex + 1)
			imgAddress = imgAddress.toString() + "2x" + suffix
			return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
				Uri.parse(BuildConfig.UPLOAD_URL_UNSAFE + imgAddress)
			} else {
				Uri.parse(BuildConfig.UPLOAD_URL + imgAddress)
			}
		}
		density > 3f -> {
			val lastDot = this.lastIndexOf(".")
			var imgAddress = this.subSequence(0, lastDot)
			val suffix = this.subSequence(lastDot, this.lastIndex + 1)
			imgAddress = imgAddress.toString() + "3x" + suffix
			return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
				Uri.parse(BuildConfig.UPLOAD_URL_UNSAFE + imgAddress)
			} else {
				Uri.parse(BuildConfig.UPLOAD_URL + imgAddress)
			}
		}
	}
	return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
		Uri.parse(BuildConfig.UPLOAD_URL_UNSAFE + this)
	} else {
		Uri.parse(BuildConfig.UPLOAD_URL + this)
	}
}

fun String.getImageUri(size: String = "", isCustomSize: Boolean = false): Uri {
	return if (this.isNotEmpty()) {
		this.getImageSize(app.resources.displayMetrics.density, size, isCustomSize)
	} else {
		Uri.parse("")
	}
}

fun String.discount(): String {
	val b = StringBuilder()
	b.append(" % ")
	b.append(this)
	b.append(" تخفیف ")
	return b.toString()
}

fun String.remain(): String {
	val b = StringBuilder()
	b.append(" تعداد باقیمانده :  ")
	b.append(this)
	b.append(" عدد ")
	return b.toString()
}

fun String.code(): String {
	val b = StringBuilder()
	b.append(" کد : ")
	b.append(this)

	return b.toString()
}

fun String.maxAmount(): String {
	val b = StringBuilder()
	b.append(" حداکثر تعداد خرید : ")
	b.append(this)
	return b.toString()
}

fun String.burnAmount(): String {
	val b = StringBuilder()
	b.append(" تعداد کدهای سوخته : ")
	b.append(this)
	return b.toString()
}

fun String.expiryDate(): String {
	val b = StringBuilder()
	b.append(" زمان انقضای آگهی : ")
	b.append(this)
	return b.toString()
}

fun String.productCount(): String {
	val b = StringBuilder()
	b.append("  تعداد تخفیف ها ")
	b.append("  ")
	b.append(this)

	return b.toString()
//    return this.toPersian()+"%" +" تخفیف"
}

fun String.boughtAmount(): String {
	val b = StringBuilder()
	b.append("تعداد محصولات خریداری شده :")
	b.append("  ")
	b.append(this)

	return b.toString()
}

fun String.canGetAmount(): String {
	val b = StringBuilder()
	b.append("مبلغ قابل برداشت :")
	b.append(" ")
	b.append(this)

	return b.toString()
}

fun String.minAmount(): String {
	val b = StringBuilder()
	b.append("حداقل مبلغ قابل برداشت")
	b.append(" ")
	b.append(this.priceFormat())
	b.append("می باشد")
	return b.toString()
}

fun String.priceFormat(): String {
	return if (this == "") {
		"۰"
	} else {
		val format = NumberFormat.getNumberInstance(Locale.US).format(this.toInt())
		("$format تومان ")
	}

}

fun String.creditCardFormat(): String {
	if (this.isEmpty())
		return ""

	val stringBuilder = StringBuilder()
	stringBuilder.append(this)
	val positions: MutableList<Int> = ArrayList()
	for (i in 1 until this.length) {
		if (i % 5 == 0 && i < this.length)
			positions.add(i - 1)
	}

	if (positions.size > 0)
		positions.forEach {
			stringBuilder.insert(it, "-")
		}

	return stringBuilder.toString()
}

fun String.width(): Int {
	val paint = Paint()
	val bounds = Rect()
	paint.typeface = Typeface.createFromAsset(ir.atriatech.extensions.app.app.assets, "fonts/app_font.ttf")
	paint.textSize = 12f
	paint.getTextBounds(this, 0, this.length, bounds)
	return bounds.width()
}

fun spannableString(
	firstString: String,
	secondString: String,
	@ColorRes firstColor: Int = 0,
	@ColorRes secondColor: Int = 0,
	firstFont: String = "fonts/app_font.ttf",
	secondFont: String = firstFont,

	thirdString: String = "",
	forthString: String = "",
	@ColorRes thirdColor: Int = firstColor,
	@ColorRes fourthColor: Int = firstColor,

	thirdFont: String = firstFont,
	fourthFont: String = firstFont
): SpannableString {
	val font1 = makeTypeFace(firstFont)
	val font2 = makeTypeFace(secondFont)
	val font3 = makeTypeFace(thirdFont)
	val font4 = makeTypeFace(fourthFont)

	val totalStringPart1 = firstString + secondString
	val endFirst = firstString.length
	val endSecond = totalStringPart1.length
	val totalStringPart2 = totalStringPart1 + thirdString
	val endThird = totalStringPart2.length
	val totalStringPart3 = totalStringPart2 + forthString
	val endFourth = totalStringPart3.length
	val wordToSpan = SpannableString(totalStringPart3)

	if (firstColor != 0)
		wordToSpan.setSpan(
			ForegroundColorSpan(ContextCompat.getColor(app.applicationContext, firstColor)),
			0,
			endFirst,
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		)

	if (secondColor != 0)
		wordToSpan.setSpan(
			ForegroundColorSpan(ContextCompat.getColor(app.applicationContext, secondColor)),
			endFirst,
			endSecond,
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		)

	if (thirdColor != 0)
		wordToSpan.setSpan(
			ForegroundColorSpan(ContextCompat.getColor(app.applicationContext, thirdColor)),
			endSecond,
			endThird,
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		)

	if (fourthColor != 0)
		wordToSpan.setSpan(
			ForegroundColorSpan(ContextCompat.getColor(app.applicationContext, fourthColor)),
			endThird,
			endFourth,
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		)

	wordToSpan.setSpan(
		CustomTypefaceSpan("", font1),
		0,
		endFirst,

		Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
	)
	wordToSpan.setSpan(
		CustomTypefaceSpan("", font2),
		endFirst,
		endSecond,

		Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
	)
	wordToSpan.setSpan(
		CustomTypefaceSpan("", font3),
		endSecond,
		endThird,


		Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
	)
	wordToSpan.setSpan(
		CustomTypefaceSpan("", font4),
		endThird,
		endFourth,
		Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
	)

	return wordToSpan
}


fun attachString(
	isUseSpace: Boolean,
	vararg strings: String
): String {
	return if (isUseSpace) {
		strings.joinToString(" ")
	} else {
		strings.joinToString("")
	}
}

fun String.addSpace(spaceSize: Int = 5): String {
	val b = StringBuilder()
	for (i in 0..spaceSize) {
		b.append(" ")
	}
	b.append(this)
	for (i in 0..spaceSize) {
		b.append(" ")
	}
	return b.toString()
}
