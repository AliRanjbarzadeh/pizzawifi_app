package ir.atriatech.pizzawifi.ui.main.profile.club

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemProfileModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class UserClubFragmentViewModel : BaseFragmentViewModel() {
	var mListItems: MutableList<ClubItemProfileModel> = mutableListOf()
	var mLoadMoreItems: MutableList<ClubItemProfileModel> = mutableListOf()

	var mAdapter: UserClubAdapter? = null
	var isLoadMore = false
	var isEnd = false
	var isRefreshing = false

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<MutableList<ClubItemProfileModel>>> by lazy {
		repository.clubUserOutcome.toLiveData(bag)
	}

	init {
		component.inject(this)
	}

	fun getData(limit: Int, offset: Int): Boolean {
		when {
			mAdapter == null -> {
				repository.clubUsedArchive(limit, offset, bag)
				return false
			}

			mListItems.isEmpty() -> {
				repository.clubUsedArchive(limit, offset, bag)
				return true
			}

			isLoadMore -> {
				repository.clubUsedArchive(limit, offset, bag)
				return true
			}
		}
		return true
	}

}
