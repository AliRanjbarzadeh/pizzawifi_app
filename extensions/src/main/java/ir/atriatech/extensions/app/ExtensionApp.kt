package ir.atriatech.extensions.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.*
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import ir.atriatech.core.BuildConfig
import ir.atriatech.core.BuildConfig.LANGUAGE_SESSION_KEY
import ir.atriatech.extensions.DEFAULT_LANGUAGE
import ir.atriatech.extensions.R
import ir.atriatech.extensions.android.loadFromSp
import timber.log.Timber

open class ExtensionApp : Application() {

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		MultiDex.install(this)
	}

	override fun onCreate() {
		super.onCreate()
		initStetho()
		initLogger()
		initTimber()
		initHawk()
		initLanguage()
		initFont()
		Ext.with(this)
	}

	private fun initTimber() {
		if (!BuildConfig.DEBUG)
			return

		Timber.plant(object : Timber.DebugTree() {
			override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
				Logger.log(priority, tag, message, t)
			}
		})
	}

	private fun initLogger() {
		if (!BuildConfig.DEBUG)
			return

		val formatStrategy = PrettyFormatStrategy.newBuilder()
			.showThreadInfo(true)
			.methodCount(2)
			.methodOffset(2)
			.tag("")
			.build()

		Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {})
	}

	private fun initStetho() {
		if (BuildConfig.DEBUG)
			Stetho.initializeWithDefaults(this)
	}

	private fun initHawk() {
		Hawk.init(this).build()
	}

	private fun initLanguage() {
		val language = loadFromSp<String>(LANGUAGE_SESSION_KEY, DEFAULT_LANGUAGE)
		setLanguage(language)
	}

	private fun initFont() {
		ViewPump.init(
			ViewPump.builder().addInterceptor(
				CalligraphyInterceptor(
					CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.app_font)).setFontAttrId(R.attr.fontPath).build()
				)
			).build()
		)
	}
}