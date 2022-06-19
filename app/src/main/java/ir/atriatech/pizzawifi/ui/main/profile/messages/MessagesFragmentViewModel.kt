package ir.atriatech.pizzawifi.ui.main.profile.messages

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.messages.Message
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class MessagesFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<Message> = mutableListOf()
    var mLoadMoreItems: MutableList<Message> = mutableListOf()
    var mAdapter: MessagesAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Message>>> by lazy {
        repository.messageOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.messageArchive(limit, offset, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.messageArchive(limit, offset, bag)
                return true
            }

            isLoadMore -> {
                repository.messageArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }

}
