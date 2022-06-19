package ir.atriatech.core.interfaces

import android.view.View

interface RecyclerViewTools {
	/*=======================Global tools========================*/
	fun <T> onItemClick(position: Int, view: View, item: T) {}
	fun <T> onDeleteClick(position: Int, view: View, item: T) {}
	fun <T> onItemSelect(position: Int, view: View, item: T) {}

	/*=======================Shop tools========================*/
	fun <T> onAddToCart(position: Int, view: View, item: T) {}
	fun <T> onAddToCart(parentPosition: Int, position: Int, view: View, item: T) {}
	fun <T> onIncreaseItem(position: Int, view: View, item: T) {}
	fun <T> onIncreaseItem(parentPosition: Int, position: Int, view: View, item: T) {}
	fun <T> onDecreaseItem(position: Int, view: View, item: T) {}
	fun <T> onDecreaseItem(parentPosition: Int, position: Int, view: View, item: T) {}
	fun <T> onRemoveItem(position: Int, view: View, item: T) {}
}