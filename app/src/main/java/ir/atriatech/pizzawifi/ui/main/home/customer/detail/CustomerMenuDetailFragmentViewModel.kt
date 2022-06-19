package ir.atriatech.pizzawifi.ui.main.home.customer.detail

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.customerMenu.Comment
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.repository.MainRepository
import ir.atriatech.pizzawifi.ui.main.profile.pizza.detail.PizzaDetailMaterialAdapter
import javax.inject.Inject

class CustomerMenuDetailFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: Pizza
    var mAdapter: PizzaDetailMaterialAdapter? = null
    var mAdapter2: CommentAdapter? = null
    var mListItems = mutableListOf<Comment>()
    var mLoadMoreItems: MutableList<Comment> = mutableListOf()
    var isLoadMore = false
    var isEnd = false
    var isComment = ObservableBoolean(false)

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Comment>>> by lazy {
        repository.commentOutcome.toLiveData(
            bag
        )
    }


    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter2 == null -> {
                repository.commentArchive(limit, offset, bag)
                return false
            }

            isLoadMore -> {
                repository.commentArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }
}
