package ir.atriatech.pizzawifi.ui.main.profile.address

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class AddressFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<Address> = mutableListOf()
    var mLoadMoreItems: MutableList<Address> = mutableListOf()
    var mAdapter: AddressAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false
    var mDeleteId = -1
    var mDeletePosition = -1

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Address>>> by lazy {
        repository.addressOutcome.toLiveData(
            bag
        )
    }
    val mDeleteObserver: LiveData<Outcome<Msg>> by lazy {
        repository.addressDeleteOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.addressArchive(limit, offset, null, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.addressArchive(limit, offset, null, bag)
                return true
            }

            isLoadMore -> {
                repository.addressArchive(limit, offset, null, bag)
                return true
            }
        }
        return true
    }

    fun delete() {
        if (mDeleteId > 0)
            repository.addressDelete(mDeleteId, bag)
    }
}
