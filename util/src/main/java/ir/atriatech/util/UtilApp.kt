package ir.atriatech.util

import android.content.Context
import androidx.multidex.MultiDex
import ir.atriatech.extensions.app.ExtensionApp
import ir.atriatech.util.dagger.*

open class UtilApp : ExtensionApp() {
	companion object {
		lateinit var utilComponent: UtilComponent
	}

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		MultiDex.install(this)
		initDi()
	}

	private fun initDi() {
		utilComponent = DaggerUtilComponent.builder().appModule(AppModule(this)).build()
	}

}