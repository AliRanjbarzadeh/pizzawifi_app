package ir.atriatech.pizzawifi.ui.main.profile.campaign.register

import android.net.Uri
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.network.createPartFromString
import ir.atriatech.extensions.network.createPartFromUri
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.campaign.CampaignDemo
import ir.atriatech.pizzawifi.repository.MainRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class CampaignRegisterFragmentViewModel : BaseFragmentViewModel() {

	var campaignObject = CampaignDemo()
	var id = 0;
	var uri: Uri? = null
	var instagram = ""

	@Inject
	lateinit var repository: MainRepository

	val mObserver: LiveData<Outcome<CampaignDemo>> by lazy { repository.campaignDemoOutcome.toLiveData(bag) }
	val mObserverSave: LiveData<Outcome<Msg>> by lazy { repository.campaignSaveOutcome.toLiveData(bag) }

	init {
		component.inject(this)
	}

	fun getData() {
		if (id != 0)
			repository.campaignDetail(id, bag)
	}

	fun saveCampaign() {
		when {
			instagram.isEmpty() -> {
				eToast("آیدی اینستاگرام خود را وارد کنید")
			}
			uri == null -> {
				eToast("یک عکس انخاب کنید")
			}
			else -> {

				val id = createPartFromString(id.toString())
				val instagram = createPartFromString(instagram)
				val fileUri: MultipartBody.Part = createPartFromUri(uri!!, "image")
				repository.campaignSave(id, instagram, fileUri, bag)
			}
		}


	}
}
