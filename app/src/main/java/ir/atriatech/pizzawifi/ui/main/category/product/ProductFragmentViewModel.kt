package ir.atriatech.pizzawifi.ui.main.category.product

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ProductFragmentViewModel : BaseFragmentViewModel() {
    lateinit var category: Category
    var mListItems = mutableListOf<Product>()
    var mLoadMoreItems: MutableList<Product> = mutableListOf()
    var mAdapter: ProductAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false
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

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.productArchive(limit, offset, category.id, branchId!!, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.productArchive(limit, offset, category.id, branchId!!, bag)
                return true
            }

            isLoadMore -> {
                repository.productArchive(limit, offset, category.id, branchId!!, bag)
                return true
            }
        }
        return true
    }
}
