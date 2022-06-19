package ir.atriatech.pizzawifi.ui.main.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import ir.atriatech.core.BuildConfig.PUSH_TOKEN_SESSION_KEY
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.network.createPartFromUri
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.UserInfo
import ir.atriatech.pizzawifi.repository.MainRepository
import ir.atriatech.pizzawifi.ui.MyMenuAdapter
import javax.inject.Inject

class ProfileFragmentViewModel : BaseFragmentViewModel() {
	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<Msg>> by lazy { repository.uploadImageOutcome.toLiveData(bag) }

	var cityModel = CityModel()
	var mAdapter: MyMenuAdapter? = null

	init {
		component.inject(this)
	}

	fun uploadImage(uri: Uri) {
		val url = createPartFromUri(uri, "image")
		repository.uploadImage(url, bag)
	}

	fun logout() {
		repository.logout(loadFromSp(PUSH_TOKEN_SESSION_KEY, ""), bag)
	}
}
