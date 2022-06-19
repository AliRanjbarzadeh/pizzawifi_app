package ir.atriatech.extensions.android

import android.view.ViewGroup

fun ViewGroup.clearChildrenFocus() {
	for (i in this.children) {
		i.clearFocus()
	}
}