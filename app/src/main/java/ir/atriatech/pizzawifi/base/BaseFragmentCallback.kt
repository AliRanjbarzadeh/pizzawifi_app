package ir.atriatech.pizzawifi.base

import android.os.Bundle
import ir.atriatech.delivercloud.base.BaseFragmentListener
import ir.atriatech.pizzawifi.entities.home.HomeBase

interface BaseFragmentCallback {
	fun registerFragmentListener(
		fragmentClassName: String,
		baseFragmentListener: BaseFragmentListener
	) {
	}

	fun unRegisterFragmentListener(fragmentClassName: String) {}
	fun changeSoftkeyMethod(isPan: Boolean = false) {}
	fun changeMenuLock(isLock: Boolean = false) {}
	fun <T> addItemToList(mItem: T) {}
	fun <T> editItemInList(mItem: T, position: Int) {}
	fun <T> deleteItemFromList(mItem: T, position: Int) {}
	fun goToShopCart() {}
	fun goToMenu() {}
	fun goToMenuDetail(argument1: Any, key1: String, argument2: Any, key2: String) {}
	fun goToOrders() {}
	fun logFirebase(eventName: String, params: Bundle?) {}
	fun handleNotification() {}
	fun handleInternalLink() {}
	fun changeMenusForLogin() {}
	fun changeBottomMenuColor(isRed: Boolean) {}

	//Check network availability == only use for dialog fragment
	fun isNetworkAvailable(): Boolean
	fun logout(isSystem: Boolean = false)
//region Home remote model
	/**
	 * Setter
	 */
	fun setHomeRemoteModel(homeRemoteModel: HomeBase)

	/**
	 * Getter
	 */
	fun getHomeRemoteModel(): HomeBase
	//endregion
}