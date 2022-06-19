package ir.atriatech.pizzawifi.ui.main.profile.pizza.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.network.createPartFromString
import ir.atriatech.extensions.network.createPartFromUri
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.repository.MainRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class PizzaDetailFragmentViewModel : BaseFragmentViewModel() {
	lateinit var mItem: Pizza
	var mAdapter: PizzaDetailMaterialAdapter? = null
	var mRightAdapter: PizzaDetailMaterialAdapter? = null
	var mLeftAdapter: PizzaDetailMaterialAdapter? = null
	var uri: Uri? = null

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<Msg>> by lazy { repository.pizzaSendToMenuOutcome.toLiveData(bag) }

	fun isHalf(): Boolean {
		val steps = mItem.pizzaMaterials.steps.filter { step -> step.rightQty > 0 || step.leftQty > 0 }
		return steps.size > 0
	}

	fun isRight(): Boolean {
		val steps = mItem.pizzaMaterials.steps.filter { step -> step.rightQty > 0 }
		return steps.size > 0
	}

	fun isLeft(): Boolean {
		val steps = mItem.pizzaMaterials.steps.filter { step -> step.leftQty > 0 }
		return steps.size > 0
	}


	init {
		component.inject(this)
	}

	fun pizzaSendToMenu() {
		if (mItem.inMenu == 0) {
			val id = createPartFromString(mItem.id.toString())
			val fileUri: MultipartBody.Part? = if (uri != null) {
				createPartFromUri(uri!!, "image")
			} else null
			repository.pizzaSendToMenu(id, fileUri, bag)
		}
	}
}
