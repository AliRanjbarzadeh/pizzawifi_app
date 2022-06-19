package ir.atriatech.util.dagger.network

import android.os.Build
import ir.atriatech.core.BuildConfig.*
import ir.atriatech.core.MY_SERIAL
import ir.atriatech.core.getUniquePsuedoID
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.saveToSp
import ir.atriatech.extensions.kotlin.e
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class MyInterceptor @Inject constructor() : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val builder = request.newBuilder()

		try {
			val originalSegments = request.url().pathSegments()
			if (originalSegments.last().toString() == "crm") {
				val pathSegments = mutableListOf<String>()
				for ((index, originalSegment) in originalSegments.withIndex()) {
					if (index < originalSegments.lastIndex) {
						pathSegments.add(originalSegment)
					}
				}
				val httpUrl = HttpUrl.parse(BASE_URL_CLUB.plus(pathSegments.joinToString("/")))!!
				builder.url(httpUrl)
			}
			var authorization = loadFromSp<String>(TOKEN_SESSION_KEY, "")
			if (authorization.isEmpty()) {
				authorization = loadFromSp(TOKEN_REGISTER_KEY, DEFAULT_TOKEN)
			}
			val language = loadFromSp<String>(LANGUAGE_SESSION_KEY, DEFAULT_LANGUAGE)

			var serial = loadFromSp<String>(MY_SERIAL, "")
			if (serial.isEmpty()) {
				serial = getUniquePsuedoID()
				saveToSp("Serial", serial)
			}

			val pushToken = loadFromSp<String>(PUSH_TOKEN_SESSION_KEY, "")

			builder.header("Cache-Control", "no-cache")
			builder.header("User-Agent", USER_AGENT)
			builder.header("AppVersion", APP_VERSION)
			builder.header("AppLanguage", language)
			builder.header("DeviceType", "1")
			builder.header("DeviceVersion", Build.VERSION.RELEASE)
			builder.header("DeviceModel", Build.MODEL)
			builder.header("DeviceManufacture", Build.MANUFACTURER)
			builder.header("DeviceSerial", serial)
			builder.header("DevicePushToken", pushToken)
			builder.header(AUTHORIZATION, TOKEN_PREFIX + authorization)
		} catch (ex: Exception) {
			e(ex.message, "HeaderErrors")
		}

		val builderResponse = chain.proceed(builder.build())
//		d(builderResponse.toString(), "RequestInterceptor")
		return builderResponse
	}
}