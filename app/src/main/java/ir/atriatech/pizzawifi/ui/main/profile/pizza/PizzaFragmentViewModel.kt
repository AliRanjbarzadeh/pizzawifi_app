package ir.atriatech.pizzawifi.ui.main.profile.pizza

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class PizzaFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<Pizza> = mutableListOf()
    var mAdapter: PizzaAdapter? = null
    var mSearchText = ""
    var mLoadMoreItems: MutableList<Pizza> = mutableListOf()
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false
    var isSearching = false
    val isSearchBox = ObservableBoolean(false)
    var mDeleteId = -1
    var mDeletePosition = -1

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Pizza>>> by lazy {
        repository.pizzaOutcome.toLiveData(
            bag
        )
    }
    val mDeleteObserver: LiveData<Outcome<Msg>> by lazy {
        repository.pizzaSendToMenuOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.pizzaArchive(limit, offset, mSearchText, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.pizzaArchive(limit, offset, mSearchText, bag)
                return true
            }

            isLoadMore -> {
                repository.pizzaArchive(limit, offset, mSearchText, bag)
                return true
            }
        }
        return true
    }

    fun delete() {
        if (mDeleteId > 0)
            repository.pizzaDelete(mDeleteId, bag)
    }
}
