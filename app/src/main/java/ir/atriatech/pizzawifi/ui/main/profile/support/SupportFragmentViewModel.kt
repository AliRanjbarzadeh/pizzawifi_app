package ir.atriatech.pizzawifi.ui.main.profile.support

import androidx.lifecycle.LiveData
import ir.atriatech.core.CoreScheduler
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.support.Support
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class SupportFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<Support> = mutableListOf()
    var mLoadMoreItems: MutableList<Support> = mutableListOf()
    var mAdapter: SupportAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false

    @Inject
    lateinit var scheduler: CoreScheduler

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Support>>> by lazy {
        repository.supportArchiveOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.supportArchive(limit, offset, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.supportArchive(limit, offset, bag)
                return true
            }

            isLoadMore -> {
                repository.supportArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }
}
