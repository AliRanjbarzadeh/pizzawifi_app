package ir.atriatech.pizzawifi.ui.main.profile.address.map

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.profile.address.AddressSearch
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class AddressMapFragmentViewModel : BaseFragmentViewModel() {

	var mListShopCart = mutableListOf<ShopCartItem>()

	var mSearchText = ""
	lateinit var mItem: Address
	var mListItems: MutableList<AddressSearch> = mutableListOf()
	var mAdapter: AddressSearchAdapter? = null
	var position = -1

	var MICROPHONE_REQUEST_CODE = 100

	var firstTimeMarkerAnim: Boolean = false

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<MutableList<AddressSearch>>> by lazy {
		repository.addressSearchOutcome.toLiveData(
			bag
		)
	}

	init {
		component.inject(this)
	}

	fun search() {
		repository.addressSearch(mSearchText, bag)
	}
}
