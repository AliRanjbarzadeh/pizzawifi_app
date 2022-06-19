package ir.atriatech.extensions.app

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import ir.atriatech.extensions.kotlin.d
import java.util.*

object Ext {
    lateinit var ctx: Application

    fun with(app: Application) {
        ctx = app
    }
}

@Suppress("DEPRECATION")
fun Application.setLanguage(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val resources = applicationContext.resources
    val configuration = resources.configuration
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        configuration.setLocale(locale)
    else
        configuration.locale = locale

    configuration.setLayoutDirection(locale)
    resources.updateConfiguration(configuration, applicationContext.resources.displayMetrics)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        this.applicationContext.createConfigurationContext(configuration)
}

var topActivity: Activity? = null

fun Application.setTopActivity() {
    this.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            topActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (topActivity == activity) {
                topActivity = null
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            topActivity = activity
        }
    })
}