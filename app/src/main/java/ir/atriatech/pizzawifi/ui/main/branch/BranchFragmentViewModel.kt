package ir.atriatech.pizzawifi.ui.main.branch

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import io.reactivex.disposables.Disposable
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class BranchFragmentViewModel : BaseFragmentViewModel() {
	var mListItems = mutableListOf<Branch>()
	var mAdapter: BranchAdapter? = null
	var isRefreshing = false
	var lastPosition = -1
	var latLng: LatLng? = null

	//Activity Result
	val GPS_RESULT = 1
	lateinit var mItem: Product
	var mCityItem: CityModel = CityModel()
	lateinit var tag: String

	var permissionDisposable: Disposable? = null
	var rxLocationDisposable: Disposable? = null

	//Location search
	val locationSearch = ObservableBoolean(false)

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<MutableList<Branch>>> by lazy {
		repository.branchOutcome.toLiveData(
			bag
		)
	}

	init {
		component.inject(this)
	}

	fun getData(): Boolean {
		when {
			mAdapter == null -> {
				rxLocationDisposable?.dispose()
				repository.branchArchive(mCityItem.id, latLng?.latitude, latLng?.longitude, bag)
				return false
			}

			mListItems.isEmpty() -> {
				rxLocationDisposable?.dispose()
				repository.branchArchive(mCityItem.id, latLng?.latitude, latLng?.longitude, bag)
				return true
			}
		}
		return true
	}

	fun getNearestBranches() {
		d("latLng?.latitude ==>  getNearestBranches ${latLng?.latitude}")

		rxLocationDisposable?.dispose() // use to stop searching request
		if (mCityItem.id == 0) {
			return
		}
		repository.branchArchive(mCityItem.id, latLng?.latitude, latLng?.longitude, bag)
	}
}
