package ir.atriatech.pizzawifi.ui.main.orders

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import ir.atriatech.core.CoreScheduler
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class OrdersFragmentViewModel : BaseFragmentViewModel() {
    var mSearchText: String = ""
    var mListItems: MutableList<Order> = mutableListOf()
    var mLoadMoreItems: MutableList<Order> = mutableListOf()
    var mAdapter: OrdersAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false
    var isSearching = false
    var isFirst = true
    val isSearchBox = ObservableBoolean(false)

    @Inject
    lateinit var repository: MainRepository

    @Inject
    lateinit var scheduler: CoreScheduler

    val mObserver: LiveData<Outcome<MutableList<Order>>> by lazy {
        repository.orderOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        val orderId = if (mSearchText.isNotEmpty()) {
            mSearchText.toInt()
        } else {
            0
        }

        when {
            mAdapter == null -> {
                repository.orderArchive(limit, offset, orderId, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.orderArchive(limit, offset, orderId, bag)
                return true
            }

            isLoadMore -> {
                repository.orderArchive(limit, offset, orderId, bag)
                return true
            }
        }
        return true
    }
}
