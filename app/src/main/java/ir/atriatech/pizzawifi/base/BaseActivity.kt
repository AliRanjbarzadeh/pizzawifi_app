package ir.atriatech.pizzawifi.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.BuildConfig.LANGUAGE_SESSION_KEY
import ir.atriatech.extensions.DEFAULT_LANGUAGE
import ir.atriatech.extensions.android.connectivityManager
import ir.atriatech.extensions.android.hideInputMethod
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.setLanguage
import ir.atriatech.extensions.app.topActivity
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.SELECTED_BRANCH_MODEL
import ir.atriatech.pizzawifi.common.SELECTED_CITY_MODEL
import ir.atriatech.pizzawifi.common.USER_INFO
import ir.atriatech.pizzawifi.common.dagger.AppDH
import ir.atriatech.pizzawifi.entities.UserInfo
import ir.atriatech.pizzawifi.entities.home.HomeBase
import ir.atriatech.pizzawifi.ui.login.LoginActivity
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity(), KeyboardVisibilityEventListener,
	BaseFragmentCallback {
	var bag = CompositeDisposable()
	var isOnPause: Boolean = false
	protected var mDialog: MaterialDialog? = null
	protected val component by lazy { AppDH.baseComponent() }

	lateinit var firebaseAnalytics: FirebaseAnalytics

	override fun attachBaseContext(newBase: Context) {
		setLanguage(newBase, AppObject.language)
		super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
		setLanguage(loadFromSp(LANGUAGE_SESSION_KEY, DEFAULT_LANGUAGE))
		// Obtain the FirebaseAnalytics instance.
		firebaseAnalytics = Firebase.analytics

		//User info
		AppObject.userInfo = loadFromSp(USER_INFO, UserInfo())

		//Load city from session
		AppObject.cityItem = loadFromSp(SELECTED_CITY_MODEL, null)
		AppObject.cityItem?.let { cityModel -> AppObject.selectedCityId = cityModel.id }

		//Load branch from session
		AppObject.branchItem = loadFromSp(SELECTED_BRANCH_MODEL, null)
		AppObject.branchItem?.let { branch -> AppObject.selectedBranchId = branch.id }
	}

	override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onPostCreate(savedInstanceState, persistentState)
		topActivity = this
	}

	override fun onPause() {
		super.onPause()
		isOnPause = true
		hideInputMethod()
	}

	override fun onResume() {
		super.onResume()
		isOnPause = false
		//Update resources
		setLanguage(AppObject.language)
	}

	override fun onStart() {
		super.onStart()

		//Keyboard listener
		KeyboardVisibilityEvent.setEventListener(this, this)
	}

	override fun onVisibilityChanged(isOpen: Boolean) {}

	override fun isNetworkAvailable(): Boolean {
		val mNetworkInfo = connectivityManager.activeNetworkInfo
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable
		}
		return false
	}

	override fun logout(isSystem: Boolean) {
		val logoutIntent = Intent(this, LoginActivity::class.java)
		logoutIntent.putExtra("logout", true)
		logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
		startActivity(logoutIntent)
		finish()
	}

	//region Home remote model
	/**
	 * Setter
	 */
	override fun setHomeRemoteModel(homeRemoteModel: HomeBase) {}

	/**
	 * Getter
	 */
	override fun getHomeRemoteModel(): HomeBase = HomeBase()
	//endregion

}
