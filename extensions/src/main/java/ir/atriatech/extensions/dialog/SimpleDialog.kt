package ir.atriatech.extensions.dialog

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog

fun Fragment.simpleDialog(title: String = "", message: String = "", @DrawableRes iconRes: Int? = null,
                          cornerRadius:Float=7f) {
	MaterialDialog(requireContext())
		.cornerRadius(cornerRadius)
		.show {
			title(text = title)
			message(text = message)
			if (iconRes!=null){
				icon(iconRes)

			}
		}
}