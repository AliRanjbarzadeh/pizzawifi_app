package ir.atriatech.extensions.kotlin

import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding> inflate(parent: ViewGroup, layoutId: Int): T {
	return DataBindingUtil.inflate(
		LayoutInflater.from(parent.context),
		layoutId,
		parent,
		false
	)
}