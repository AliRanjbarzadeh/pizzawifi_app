package ir.atriatech.pizzawifi.ui.main.profile.campaign

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.campaign.Campaign
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class CampaignListFragmentViewModel : BaseFragmentViewModel() {
    var mListItems: MutableList<Campaign> = mutableListOf()
    var mLoadMoreItems: MutableList<Campaign> = mutableListOf()
    var mAdapter: CampaignAdapter? = null
    var isLoadMore = false
    var isEnd = false
    var isRefreshing = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Campaign>>> by lazy {
        repository.campaignOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(limit: Int, offset: Int): Boolean {
        when {
            mAdapter == null -> {
                repository.campaignArchive(limit, offset, bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.campaignArchive(limit, offset, bag)
                return true
            }

            isLoadMore -> {
                repository.campaignArchive(limit, offset, bag)
                return true
            }
        }
        return true
    }

}
