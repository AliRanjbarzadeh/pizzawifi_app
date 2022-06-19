package ir.atriatech.pizzawifi.ui.main.profile.club

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ClubFragmentViewModel : BaseFragmentViewModel() {
	var mListItems: MutableList<ClubItemModel> = mutableListOf()
	var mLoadMoreItems: MutableList<ClubItemModel> = mutableListOf()

	var mAdapter: ClubAdapter? = null
	var isLoadMore = false
	var isEnd = false
	var isRefreshing = false

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<MutableList<ClubItemModel>>> by lazy {
		repository.clubOutcome.toLiveData(bag)
	}

	val mUseObserver: LiveData<Outcome<Msg>> by lazy {
		repository.clubUseOutcome.toLiveData(bag)
	}

	init {
		component.inject(this)
	}

	fun getData(limit: Int, offset: Int): Boolean {
		when {
			mAdapter == null -> {
				repository.clubArchive(limit, offset, bag)
				return false
			}

			mListItems.isEmpty() -> {
				repository.clubArchive(limit, offset, bag)
				return true
			}

			isLoadMore -> {
				repository.clubArchive(limit, offset, bag)
				return true
			}
		}
		return true
	}

	fun useGift(id: Int) {
		repository.clubUse(id, bag)
	}

}
