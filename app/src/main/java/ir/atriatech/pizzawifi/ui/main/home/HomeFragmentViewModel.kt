package ir.atriatech.pizzawifi.ui.main.home

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.disposables.Disposable
import ir.atriatech.core.BuildConfig
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.saveToSp
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.SHOULD_REFRESH_HOME
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.entities.home.HomeBase
import ir.atriatech.pizzawifi.repository.MainRepository
import ir.atriatech.pizzawifi.ui.main.branch.BranchAdapter
import ir.atriatech.pizzawifi.ui.main.city.CityAdapter
import javax.inject.Inject

class HomeFragmentViewModel : BaseFragmentViewModel() {
	var mListItems = mutableListOf<Any>()
	var categories = arrayListOf<Category>()
	var mAdapter: HomeAdapter? = null
	var mCityAdapter: CityAdapter? = null
	var mBranchAdapter: BranchAdapter? = null
	var mItem: HomeBase = HomeBase()
	var lastPosition = -1
	var lastPositionBranch = -1
	var isShowBranch = false
	var bottomSheetTitle = ObservableField<String>()

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<HomeBase>> by lazy { repository.homeOutcome.toLiveData(bag) }

	val mBranchObserver: LiveData<Outcome<MutableList<Branch>>> by lazy {
		repository.branchOutcome.toLiveData(
			bag
		)
	}
	var latLng: LatLng? = null

	//Location search
	val locationSearch = ObservableBoolean(false)
	var permissionDisposable: Disposable? = null
	var rxLocationDisposable: Disposable? = null
	//Activity Result
	val GPS_RESULT = 1

	init {
		component.inject(this)
	}

	fun getData(): Boolean {
		if (mAdapter == null || SHOULD_REFRESH_HOME) {
			repository.home(AppObject.branchItem?.id ?: 0, bag)
			return false;
		}
		return true
	}

	fun savePushToken() {
		FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
			instanceIdResult?.token?.let { pushToken ->
				saveToSp(BuildConfig.PUSH_TOKEN_SESSION_KEY, pushToken)
				repository.savePushToken(pushToken, bag)
			}
		}
	}

	fun getNearestBranches() {
		rxLocationDisposable?.dispose() // use to stop searching request
		if (AppObject.selectedCityId == 0) {
			return
		}
		repository.branchArchive(AppObject.selectedCityId, latLng?.latitude, latLng?.longitude, bag)
	}
}
