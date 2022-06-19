package ir.atriatech.extensions.android

import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.atriatech.extensions.R
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.app.app
import ir.atriatech.extensions.kotlin.e


fun AppCompatActivity.shareText(shareBody: String, shareTitle: String = " اشتراک گذاری در ") {
	val sharingIntent = Intent(Intent.ACTION_SEND)
	sharingIntent.type = "text/plain"
	sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
	startActivity(Intent.createChooser(sharingIntent, shareTitle))
}

fun AppCompatActivity.emailTo(emailAddress: String) {
	try {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$emailAddress"))
		intent.putExtra(Intent.EXTRA_SUBJECT, "لاویا")
		intent.putExtra(Intent.EXTRA_TEXT, "\nارسال شده از اپلیکیشن لاویا")
		startActivity(intent)
	} catch (e: Exception) {
		e(e.message)
		eToast("برنامه ای برای باز کردن وجود ندارد")
	}
}

fun AppCompatActivity.dialThisNumber(phone: String) {
	try {
		val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
		startActivity(intent)
	} catch (e: Exception) {
		e(e.message)
		eToast("برنامه ای برای باز کردن وجود ندارد")

	}
}


fun AppCompatActivity.copyToClipboard(title: String, value: String) {
	val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
	val clip = ClipData.newPlainText(title, value)
	clipboard?.setPrimaryClip(clip)
	sToast(getString(R.string.save_in_clip_boar))
}

fun Fragment.shareText(shareBody: String, shareTitle: String = " Share With ") {
	val sharingIntent = Intent(Intent.ACTION_SEND)
	sharingIntent.type = "text/plain"
	sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
	startActivity(Intent.createChooser(sharingIntent, shareTitle))
}

fun Fragment.emailTo(emailAddress: String, subject: String = "", suffix: String = "") {
	try {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$emailAddress"))
		intent.putExtra(Intent.EXTRA_SUBJECT, subject)
		intent.putExtra(Intent.EXTRA_TEXT, "\n$suffix")
		startActivity(intent)
	} catch (e: Exception) {
		e(e.message)
		eToast(getString(R.string.no_app_found_to_open))
	}
}

fun Fragment.dialThisNumber(phone: String) {
	try {
		val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
		startActivity(intent)
	} catch (e: Exception) {
		e(e.message)
		eToast(getString(R.string.no_app_found_to_open))

	}
}

fun Fragment.startAction(action: String) {

	try {
		val uri = action
		val intent = Intent(Intent.ACTION_VIEW)
		intent.data = Uri.parse(uri)
		startActivity(intent)
	} catch (e: Exception) {
		e(e.message)
		eToast(getString(R.string.no_app_found_to_open))

	}
}

fun AppCompatActivity.startAction(action: String) {

	try {
		val uri = action
		val intent = Intent(Intent.ACTION_VIEW)
		intent.data = Uri.parse(uri)
		startActivity(intent)
	} catch (e: Exception) {
		e(e.message)
		eToast(getString(R.string.no_app_found_to_open))

	}
}


fun Fragment.copyToClipboard(title: String, value: String) {
	val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
	val clip = ClipData.newPlainText(title, value)
	clipboard?.setPrimaryClip(clip)
	sToast(getString(R.string.save_in_clip_boar))
}
