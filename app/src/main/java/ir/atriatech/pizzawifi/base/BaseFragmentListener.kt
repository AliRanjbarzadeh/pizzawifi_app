package ir.atriatech.delivercloud.base

interface BaseFragmentListener {
    fun <T> addItemToList(mItem: T) {}
    fun <T> editItemInList(mItem: T, position: Int) {}
    fun <T> deleteItemFromList(mItem: T, position: Int) {}
    fun goToMenu() {}
    fun goToMenuDetail(argument1: Any, key1: String, argument2: Any, key2: String) {}
    fun sureOnBack() {}
    fun handleNotification() {}
    fun handlePayment() {}
    fun updateValues() {}
}