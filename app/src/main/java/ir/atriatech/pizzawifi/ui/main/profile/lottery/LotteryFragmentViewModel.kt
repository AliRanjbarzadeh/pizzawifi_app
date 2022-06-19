package ir.atriatech.pizzawifi.ui.main.profile.lottery

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.lottery.LotteryModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class LotteryFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<LotteryModel> = mutableListOf()
    var mLoadMoreItems: MutableList<LotteryModel> = mutableListOf()
    var mAdapter: LotteryAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<LotteryModel>>> by lazy {
        repository.lotteryOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.lotteryArchive(limit, offset, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.lotteryArchive(limit, offset, bag)
                return true
            }

            isLoadMore -> {
                repository.lotteryArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }

}
