package ir.atriatech.extensions.dialog

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import ir.atriatech.extensions.R
import ir.atriatech.extensions.android.finishAffinity
import ir.atriatech.extensions.app.isNetworkConnected

fun Fragment.connectionCheck(connectionCheck: ConnectionCheck) {
	if (isNetworkConnected()) {
		connectionCheck.isConnected()
	} else {
		context?.let {
			MaterialDialog(it)
				.apply {
					cancelable(false)
					noAutoDismiss()
					title(R.string.error)
					message(R.string.check_internet)
					positiveButton(R.string._retry)
						.positiveButton {
							if (isNetworkConnected()) {
								connectionCheck.isConnected()
								dismiss()
							}
						}
					negativeButton(R.string._close)
						.negativeButton {
							finishAffinity()
						}
					show()
				}
		}
	}
}

fun Fragment.connectionDialog(): MaterialDialog? {
	return context?.let {
		MaterialDialog(it)
			.show {
				noAutoDismiss()
				cancelable(false)
				cancelOnTouchOutside(false)
				title(R.string.error)
				message(R.string.message)
				positiveButton(R.string.retry)
			}
	}
}

fun AppCompatActivity.connectionCheck(connectionCheck: ConnectionCheck) {
	if (isNetworkConnected()) {
		connectionCheck.isConnected()
	} else {
		MaterialDialog(this)
			.show {
				noAutoDismiss()
				cancelable(false)
				cancelOnTouchOutside(false)
				title(R.string.error)
				message(R.string.message)
				positiveButton(R.string.retry)
					.positiveButton { materialDialog ->
						if (isNetworkConnected()) {
							materialDialog.dismiss()
							connectionCheck.isConnected()
						}

					}
					.negativeButton { materialDialog ->
						materialDialog.dismiss()
						finishAffinity()
					}

			}
	}
}

fun connectionCheck(connectionCheck: ConnectionCheck, activity: Activity) {
	if (isNetworkConnected()) {
		connectionCheck.isConnected()
	} else {
		activity.let { topActivity ->
			MaterialDialog(topActivity)
				.apply {
					cancelable(false)
					noAutoDismiss()
					title(R.string.error)
					message(R.string.check_internet)
					positiveButton(R.string.c1)
						.positiveButton {
							topActivity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
						}
					negativeButton(R.string._retry)
						.negativeButton {
							if (isNetworkConnected()) {
								dismiss()
								connectionCheck.isConnected()
							}
						}
					show()
				}
		}
	}
}

interface ConnectionCheck {
	fun isConnected()
}

