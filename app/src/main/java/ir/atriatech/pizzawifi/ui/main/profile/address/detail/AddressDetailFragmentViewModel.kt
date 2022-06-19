package ir.atriatech.pizzawifi.ui.main.profile.address.detail

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.isMobile
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class AddressDetailFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: Address
    lateinit var target: LatLng
    var name = ""
    var mobile = ""
    var address = ""
    var position = -1
    var forceAdd = 0
    var branchId: Int? = null
    var isFromShopCart = false
    var mListShopCart = mutableListOf<ShopCartItem>()

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Msg>> by lazy { repository.addressAddOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun saveAddress() {
        if (name.isEmpty()) {
            eToast(findString(R.string.emptyName))
            return
        }
        if (mobile.isEmpty()) {
            eToast(findString(R.string.emptyMobile))
            return
        }
        if (!mobile.isMobile()) {
            eToast(findString(R.string.wrongMobile))
            return
        }
        if (address.isEmpty()) {
            eToast(findString(R.string.emptyAddress2))
            return
        }
        if (isFromShopCart) {
            branchId = AppObject.selectedBranchId
        }

        if (mItem.id == 0) {
            repository.addressAdd(
                name = name,
                mobile = mobile,
                address = address,
                lat = target.latitude,
                lng = target.longitude,
                forceAdd = forceAdd,
                branchId = branchId,
                bag = bag
            )
        } else {
            repository.addressEdit(
                id = mItem.id,
                name = name,
                mobile = mobile,
                address = address,
                lat = target.latitude,
                lng = target.longitude,
                forceAdd = forceAdd,
                bag = bag
            )
        }
    }
}
