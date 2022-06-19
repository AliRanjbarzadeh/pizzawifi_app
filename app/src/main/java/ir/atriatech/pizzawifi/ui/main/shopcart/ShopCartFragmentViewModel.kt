package ir.atriatech.pizzawifi.ui.main.shopcart

import androidx.databinding.ObservableField
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem

class ShopCartFragmentViewModel : BaseFragmentViewModel() {
    var mListItems = mutableListOf<ShopCartItem>()
    var mAdapter: ShopCartAdapter? = null
    var mPrice = ObservableField<String>("")

    init {
        component.inject(this)
    }
}
