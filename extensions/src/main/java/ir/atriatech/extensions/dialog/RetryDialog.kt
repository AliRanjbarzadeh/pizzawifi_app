package ir.atriatech.extensions.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import ir.atriatech.extensions.R
import ir.atriatech.extensions.android.finishAffinity
import ir.atriatech.extensions.app.topActivity

fun Fragment.retry(retry: Retry, txtTitle: String = "لطفا اتصال اینترنت خود را بررسی کنید", isFinishApp: Boolean = true) {
	context?.let {
		MaterialDialog(it)
			.apply {
				title(R.string.error)
				message(text = txtTitle)
				noAutoDismiss()
				cancelable(false)
				positiveButton(R.string._retry)
					.positiveButton {
						connectionCheck(object : ConnectionCheck {
							override fun isConnected() {
								retry.onRetry()
							}
						})
						dismiss()
					}
				negativeButton(R.string._close)
					.negativeButton {
						if (isFinishApp) {
							finishAffinity()
						} else {
							dismiss()
							topActivity?.onBackPressed()
						}
					}
				show()
			}
	}
}

fun AppCompatActivity.retry(retry: Retry, txtTitle: String = "خطا در برقراری ارتباط") {
	MaterialDialog(this)
		.apply {
			title(R.string.error)
			message(text = txtTitle)
			noAutoDismiss()
			cancelable(false)
			positiveButton(R.string._retry)
				.positiveButton {
					connectionCheck(object : ConnectionCheck {
						override fun isConnected() {
							retry.onRetry()
						}
					})
					dismiss()
				}
			negativeButton(R.string._close)
				.negativeButton {
					finishAffinity()
				}
			show()
		}
}

interface Retry {
	fun onRetry()
}