package ir.atriatech.pizzawifi.ui.main.productdetail

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.ProductMaterial
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ProductDetailFragmentViewModel : BaseFragmentViewModel() {
    var materialAdapter: ProductMaterialAdapter? = null
    var mListItems = mutableListOf<ProductMaterial>()
    var tag: String = ""
    lateinit var mItem: Product
    var isLoaded = false
    var branchId: Int? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Product>> by lazy {
        repository.productDetailOutcome.toLiveData(
            bag
        )
    }


    init {
        component.inject(this)
    }

    fun increaseFake() {
        mItem.fakeCount++
    }

    fun decreaseFake() {
        if (mItem.fakeCount > 1)
            mItem.fakeCount--
    }

    fun getData() {
        if (mItem.id != 0 && branchId != null) {
            repository.productDetail(mItem.id, branchId!!, bag)
        }
    }
}
