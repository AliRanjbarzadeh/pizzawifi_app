package ir.atriatech.pizzawifi.ui.main.more.branches

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class MoreBranchFragmentViewModel : BaseFragmentViewModel() {
	var mAdapter: MoreBranchCityAdapter? = null
	var mListItems = mutableListOf<CityModel>()
	var isRefreshing = false

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<MutableList<CityModel>>> by lazy { repository.moreBranchesOutcome.toLiveData(bag) }

	init {
		component.inject(this)
	}

	fun getData(): Boolean {
		when {
			mAdapter == null -> {
				repository.moreBranches(bag)
				return false
			}

			mListItems.isEmpty() -> {
				repository.moreBranches(bag)
				return true
			}
		}
		return true
	}
}
