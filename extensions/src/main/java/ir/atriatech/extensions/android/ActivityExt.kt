package ir.atriatech.extensions.android

import android.app.Activity
import android.app.Service
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import ir.atriatech.extensions.kotlin.d
import java.util.*

/**
 * Created by Victor on 2017/9/12. (ง •̀_•́)ง
 */

inline fun <reified T : Activity> Activity.goActivity() = startActivity(Intent(this, T::class.java))

inline fun <reified T : Activity> Activity.goActivity(requestCode: Int) = startActivityForResult(Intent(this, T::class.java), requestCode)

inline fun <reified T : Service> Activity.goService() = startService(Intent(this, T::class.java))

inline fun <reified T : Service> Activity.goService(sc: ServiceConnection, flags: Int = Context.BIND_AUTO_CREATE) = bindService(Intent(this, T::class.java), sc, flags)

fun Activity.hideInputMethod() = window.peekDecorView()?.let { inputMethodManager.hideSoftInputFromWindow(window.peekDecorView().windowToken, 0) }

fun Activity.showInputMethod(v: EditText) {
	v.requestFocus()
	inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED)
}

fun Activity.clearWindowBackground() = window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

fun Activity.steepStatusBar() {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
	}
}

fun AppCompatActivity.addFragments(fragments: List<Fragment>, containerId: Int) {
	fragments.forEach {
		val ft = supportFragmentManager.beginTransaction()
		ft.add(containerId, it)
		ft.commitAllowingStateLoss()
	}
}

fun AppCompatActivity.showFragment(fragment: Fragment) {
	val ft = supportFragmentManager.beginTransaction()
	ft.show(fragment)
	ft.commitAllowingStateLoss()
}

fun AppCompatActivity.hideFragment(fragment: Fragment) {
	val ft = supportFragmentManager.beginTransaction()
	ft.hide(fragment)
	ft.commitAllowingStateLoss()
}


@Suppress("DEPRECATION")
fun Activity.setLanguage(language: String) {
	val locale = Locale(language)
	Locale.setDefault(locale)

	val resources = this.applicationContext.resources
	val configuration = resources.configuration
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) configuration.setLocale(locale)
	else configuration.locale = locale

	configuration.setLayoutDirection(locale)
	this.resources.updateConfiguration(
		configuration, this.applicationContext.resources.displayMetrics
	)
	resources.updateConfiguration(configuration, this.applicationContext.resources.displayMetrics)

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) this.applicationContext.createConfigurationContext(configuration)
}

@Suppress("DEPRECATION")
fun Activity.setLanguage(context: Context, language: String) {
	val locale = Locale(language)
	Locale.setDefault(locale)

	val resources = context.resources
	val configuration = resources.configuration
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) configuration.setLocale(locale)
	else configuration.locale = locale

	configuration.setLayoutDirection(locale)
	resources.updateConfiguration(configuration, resources.displayMetrics)

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.createConfigurationContext(configuration)
}

fun AppCompatActivity.callOnActivityResultForChildren(requestCode: Int, resultCode: Int, data: Intent?) {
	try {
		val fm = supportFragmentManager
		if (fm.fragments.size > 0) {
			for (i in 0 until fm.fragments.size) {
				val fragment = fm.fragments[i]
				when (fragment is NavHostFragment) {
					true -> {
						val childFragments = fragment.childFragmentManager.fragments
						childFragments.forEach { child ->
							child.onActivityResult(requestCode, resultCode, data)
						}
					}
				}
			}
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}
}
