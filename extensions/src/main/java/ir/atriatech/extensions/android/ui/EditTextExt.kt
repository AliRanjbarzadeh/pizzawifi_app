package ir.atriatech.extensions.android.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.extensions.kotlin.toEnglish
import java.util.*

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
	this.addTextChangedListener(object : TextWatcher {
		override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
		}

		override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
		}

		override fun afterTextChanged(editable: Editable?) {
			afterTextChanged.invoke(editable?.trim().toString())
		}
	})
}

fun EditText.formatPrice() {
	this.addTextChangedListener(object : TextWatcher {
		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		}

		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		}

		override fun afterTextChanged(editable: Editable?) {
			this@formatPrice.removeTextChangedListener(this)
			var value = editable.toString().replace(",", "")
			value = value.toEnglish()
			if (value.isNotEmpty()) {
				if (value.toInt() > 100000000) {
					this@formatPrice.setText((100000000).priceFormat(""))
				} else {
					this@formatPrice.setText(value.toInt().priceFormat(""))
				}
			}
			this@formatPrice.setSelection(this@formatPrice.text.length)
			this@formatPrice.addTextChangedListener(this)
		}

	})
}

fun EditText.afterDelayTextChanged(afterTextChanged: (String) -> Unit, time: Long = 200, activity: FragmentActivity): TextWatcher {
	var timer: Timer? = null
	val textWatcher = object : TextWatcher {
		override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
		}

		override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
			timer?.cancel()
		}

		override fun afterTextChanged(editable: Editable?) {
			timer = Timer()
			timer?.schedule(object : TimerTask() {
				override fun run() {
					activity.runOnUiThread {
						afterTextChanged.invoke(editable?.trim().toString())
					}
				}
			}, time)
		}
	}
	this.addTextChangedListener(textWatcher)
	return textWatcher
}
