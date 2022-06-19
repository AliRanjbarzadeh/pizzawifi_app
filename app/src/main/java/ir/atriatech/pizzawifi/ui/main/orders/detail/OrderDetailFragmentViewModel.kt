package ir.atriatech.pizzawifi.ui.main.orders.detail

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.OrderItem
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class OrderDetailFragmentViewModel : BaseFragmentViewModel() {
    var mID = 0
    var mItem = ObservableField<Order>()
    var mAdapter: OrderItemsAdapter? = null
    var isRefresh: Boolean = false
    var mListItems = mutableListOf<OrderItem>()

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Order>> by lazy { repository.orderDetailOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        when {
            mAdapter == null -> {
                repository.orderDetail(mID, bag)
                return false
            }

            isRefresh -> {
                repository.orderDetail(mID, bag)
                return false
            }
        }
        return true
    }
}
