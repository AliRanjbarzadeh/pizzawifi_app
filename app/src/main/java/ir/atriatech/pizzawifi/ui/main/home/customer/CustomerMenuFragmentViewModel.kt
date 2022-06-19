package ir.atriatech.pizzawifi.ui.main.home.customer

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class CustomerMenuFragmentViewModel : BaseFragmentViewModel() {
    var mListItems = mutableListOf<Pizza>()
    var mAdapter: CustomerMenuAdapter? = null
    var mLoadMoreItems: MutableList<Pizza> = mutableListOf()
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Pizza>>> by lazy {
        repository.customerOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.customerArchive(limit, offset, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.customerArchive(limit, offset, bag)
                return true
            }

            isLoadMore -> {
                repository.customerArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }
}
