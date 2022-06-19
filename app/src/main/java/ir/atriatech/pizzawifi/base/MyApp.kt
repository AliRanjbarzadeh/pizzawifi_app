package ir.atriatech.pizzawifi.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.webkit.WebView
import com.google.firebase.messaging.FirebaseMessagingService
import ir.atriatech.core.BuildConfig.LANGUAGE_SESSION_KEY
import ir.atriatech.extensions.DEFAULT_LANGUAGE
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.app.setTopActivity
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.entities.UserInfo
import ir.atriatech.util.UtilApp

class MyApp : UtilApp() {

	override fun onCreate() {
		super.onCreate()
		notificationInit()
//        Crashlytics.Builder()
//            .build()
		setTopActivity()
		AppObject.language = loadFromSp(LANGUAGE_SESSION_KEY, DEFAULT_LANGUAGE)

		//User info
		AppObject.userInfo = loadFromSp(USER_INFO, UserInfo())

		//Load city from session
		AppObject.cityItem = loadFromSp(SELECTED_CITY_MODEL, null)
		AppObject.cityItem?.let { cityModel -> AppObject.selectedCityId = cityModel.id }

		//Load branch from session
		AppObject.branchItem = loadFromSp(SELECTED_BRANCH_MODEL, null)
		AppObject.branchItem?.let { branch -> AppObject.selectedBranchId = branch.id }

		//Destroy web view in android 7 and aboive is necessary because it changes locale resource
		WebView(this).destroy()
	}

	private fun notificationInit() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = "Defaults"
			val description = "Orders, Supports, Messages"
			val importance = NotificationManager.IMPORTANCE_HIGH

			//Channel
			val channel = NotificationChannel(CHANNEL_ID, name, importance)
			channel.description = description
			channel.enableVibration(true)
			channel.enableLights(true)
			channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

			//Notification Manager
			val notificationManager =
				getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
			try {
				notificationManager.deleteNotificationChannel(CHANNEL_ID)
			} catch (ex: Exception) {
			}
			notificationManager.createNotificationChannel(channel)
		}
	}
}