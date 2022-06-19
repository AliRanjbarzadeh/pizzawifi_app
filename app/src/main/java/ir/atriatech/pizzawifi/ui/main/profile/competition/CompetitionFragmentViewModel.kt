package ir.atriatech.pizzawifi.ui.main.profile.competition

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.competition.Competition
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class CompetitionFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<Competition> = mutableListOf()
    var mLoadMoreItems: MutableList<Competition> = mutableListOf()
    var mAdapter: CompetitionAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Competition>>> by lazy {
        repository.competitionOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.competitionArchive(limit, offset, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.competitionArchive(limit, offset, bag)
                return true
            }

            isLoadMore -> {
                repository.competitionArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }
}
