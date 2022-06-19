package ir.atriatech.pizzawifi.ui.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import ir.atriatech.core.BuildConfig.LOGIN_SESSION_KEY
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseActivity
import ir.atriatech.pizzawifi.common.BYPASS_LOGIN
import ir.atriatech.pizzawifi.common.USER_INFO
import ir.atriatech.pizzawifi.entities.UserInfo
import ir.atriatech.pizzawifi.ui.login.LoginActivity
import ir.atriatech.pizzawifi.ui.main.MainActivity

class IntroActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_intro)

		val handler = Handler()
		handler.postDelayed({
			initValues()
		}, 1000)
	}

	fun initValues() {
		val isLogin = loadFromSp<Boolean>(LOGIN_SESSION_KEY, false)
		val userInfo = loadFromSp<UserInfo>(USER_INFO, UserInfo())
		val bypassLogin = loadFromSp<Boolean>(BYPASS_LOGIN, true)

		if ((userInfo.mobile.isNotEmpty() && userInfo.name.isNotEmpty() && isLogin) || bypassLogin) {
			val intent = Intent(this, MainActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)

			startActivity(intent)
			finish()
		} else {
			val intent = Intent(this, LoginActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)

			startActivity(intent)
			finish()
		}
	}
}
