package ir.atriatech.pizzawifi.ui.main.shopcart.offer

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class OfferDialogViewModel : BaseFragmentViewModel() {
    lateinit var category: Category
    var mItem: ShopCartOfferModel? = null
    var mAdapter: ProductOfferAdapter? = null

    //Loading
    val isLoaded = ObservableBoolean(false)
    val loading = ObservableBoolean(false)

    //Connection error
    val connectionError = ObservableBoolean(false)

    var branchId: Int? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Product>>> by lazy {
        repository.productOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    /*fun getData(){
        if (branchId != null){
            repository.productOfferArchive(branchId!!, bag)
        }
    }
     */
}
