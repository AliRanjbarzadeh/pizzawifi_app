package ir.atriatech.pizzawifi.ui.main.shopcart

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartDecide
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ShopCartDecideFragmentViewModel : BaseFragmentViewModel() {

    var mListItems = mutableListOf<ShopCartItem>()

    var shopCartDecide: ShopCartDecide? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<ShopCartDecide>> by lazy {
        repository.shopCartDecideOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }


    fun getData(): Boolean {
        d("AppObject.branchShopCart --> ${AppObject.selectedBranchId}")
        d("AppObject.branchesItem --> ${AppObject.branchItem?.id}")

        when {
            shopCartDecide == null -> {
                if (AppObject.selectedBranchId != null) {
                    repository.shopCartDecide(AppObject.selectedBranchId!!, bag)
                    return false
                }
            }
        }
        return true
    }
}
