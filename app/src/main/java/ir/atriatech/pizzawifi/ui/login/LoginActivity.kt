package ir.atriatech.pizzawifi.ui.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import ir.atriatech.core.BuildConfig.TOKEN_REGISTER_KEY
import ir.atriatech.core.BuildConfig.TOKEN_SESSION_KEY
import ir.atriatech.extensions.android.deleteFromSp
import ir.atriatech.extensions.android.toast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.app.topActivity
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseActivity
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.SMS_CODE
import ir.atriatech.pizzawifi.common.SMS_RECEIVED
import ir.atriatech.pizzawifi.common.WHICH_MENU_CLICKED
import ir.atriatech.pizzawifi.ui.login.step2.LoginStep2Fragment
import ir.atriatech.pizzawifi.ui.login.step3.LoginStep3Fragment
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {

	private var lastMillis: Long = 0
	var whichMenu: Int = -1

	private val broadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent == null || intent.action == null || intent.extras == null)
				return

			when (intent.action) {
				SMS_RECEIVED -> {
					if (navHost.childFragmentManager.fragments[0] is LoginStep2Fragment) {
						val fragment =
							navHost.childFragmentManager.fragments[0] as LoginStep2Fragment
						val code = intent.extras?.getString(SMS_CODE)
						fragment.setCode(code)
					}
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

//		val serial = loadFromSp<String>(MY_SERIAL, "")
//		val language = loadFromSp<String>(LANGUAGE_SESSION_KEY, DEFAULT_LANGUAGE)
//
//		var badgeCount = 0
//		if (existInSp(NOTIFICATION_BADGE_COUNT)) {
//			badgeCount = loadFromSp<Int>(NOTIFICATION_BADGE_COUNT, 0)
//		}
//
//		deleteAllInSp()
//		saveToSp(MY_SERIAL, serial)
//		saveToSp(LANGUAGE_SESSION_KEY, language)
//		saveToSp(NOTIFICATION_BADGE_COUNT, badgeCount)
		deleteFromSp(TOKEN_SESSION_KEY)
		topActivity = this

		whichMenu = intent.getIntExtra(WHICH_MENU_CLICKED, -1)
	}

	override fun onResume() {
		super.onResume()

		//region Receiver
		val intentFilter = IntentFilter()
		intentFilter.addAction(SMS_RECEIVED)

		registerReceiver(broadcastReceiver, intentFilter)
		//endregion
	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(broadcastReceiver)
		deleteFromSp(TOKEN_REGISTER_KEY)
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		(navHost.childFragmentManager.fragments[0] as BaseFragment).onVisibilityChanged(isOpen)
	}

	override fun onBackPressed() {
		if (navHost.childFragmentManager.fragments[0] !is LoginStep3Fragment) {
			super.onBackPressed()
		} else {
			val currentMillis = System.currentTimeMillis()
			if (lastMillis > 0 && TimeUnit.MILLISECONDS.toSeconds(currentMillis - lastMillis) < 3) {
				finish()
			} else {
				lastMillis = currentMillis
				toast(findString(R.string.exitRegisterWarning))
			}
		}
	}

	override fun logFirebase(eventName: String, params: Bundle?) {
//		firebaseAnalytics.logEvent(eventName, params)
	}
}
