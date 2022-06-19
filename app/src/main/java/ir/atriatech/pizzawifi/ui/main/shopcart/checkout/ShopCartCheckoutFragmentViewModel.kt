package ir.atriatech.pizzawifi.ui.main.shopcart.checkout

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.DELIVER_TYPE
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.shopcart.CheckoutPrices
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartTools
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ShopCartCheckoutFragmentViewModel : BaseFragmentViewModel() {
	var mListItems = mutableListOf<ShopCartItem>()
	var checkoutPrices = CheckoutPrices()
	var address: Address? = null
	var branch: Branch? = null
	var description: String = ""
	var isWallet = 0
	var orderJson = ""
	var forceAddAddress: Int = 0
	var forceAddTime: Int = 0
	var mOrderAdd: Msg? = null
	var isFromBrowser = false
	var isTools = false
	var shopCartTools = ShopCartTools()
	var lastReserveTimePosition = -1

	@Inject
	lateinit var repository: MainRepository
	val mToolsObserver: LiveData<Outcome<ShopCartTools>> by lazy {
		repository.toolsOutcome.toLiveData(
			bag
		)
	}
	val mDiscountObserver: LiveData<Outcome<Msg>> by lazy {
		repository.discountOutcome.toLiveData(
			bag
		)
	}
	val mOrderAddObserver: LiveData<Outcome<Msg>> by lazy {
		repository.oredrAddOutcome.toLiveData(
			bag
		)
	}
	val mOrderCheckPaymentObserver: LiveData<Outcome<Msg>> by lazy {
		repository.oredrCheckPaymentOutcome.toLiveData(
			bag
		)
	}

	init {
		component.inject(this)
	}

	fun getTools() {
		if (isTools) {
			return
		}

		d("AppObject.branchShopCart != null && address DELIVER_TYPE ${AppObject.selectedBranchId}  ${address} $DELIVER_TYPE")
		if (AppObject.selectedBranchId != null) {
			if (DELIVER_TYPE == 3) {
				if (address != null) {
					d("AppObject.branchShopCart != null && address  ${AppObject.selectedBranchId}  ${address!!.id}")
					repository.shopCartTools(address!!.id, AppObject.selectedBranchId!!, bag)
				}
			} else {
				d("AppObject.branchShopCart != null && address  ${AppObject.selectedBranchId} ")
				val addressId = null
				repository.shopCartTools(addressId, AppObject.selectedBranchId!!, bag)
			}
		}
	}

	fun checkDiscount() {
		if (checkoutPrices.discountCode.isEmpty()) {
			eToast(findString(R.string.enterDiscountCode))
			return
		}
		if (branch == null) {
			eToast(findString(R.string.noBranchSelected))
			return
		}
		repository.checkDiscount(checkoutPrices.discountCode, branch!!.id, bag)
	}

	fun addOrder() {
		if (mListItems.isEmpty() || orderJson.isEmpty()) {
			eToast(findString(R.string.emptyBasket))
			return
		}
		if (address == null && branch == null) {
			eToast(findString(R.string.emptyAddress3))
			return
		}

		if (checkoutPrices.orderTime !in arrayOf(1, 2)) {
			eToast(findString(R.string.selectOrderTime))
			return
		}

		if (checkoutPrices.paymentType != 1 && checkoutPrices.paymentType != 2) {
			eToast(findString(R.string.selectPaymentMethod))
			return
		}

		repository.addOrder(
			discountCode = checkoutPrices.discountCode,
			isUseWallet = isWallet,
			orderJson = orderJson,
			addressId = address?.id ?: 0,
			branchId = branch?.id ?: 0, // todo: edit in the future ======================
			forceAddAddress = forceAddAddress,
			forceAddTime = forceAddTime,
			deliverType = DELIVER_TYPE,
			paymentType = checkoutPrices.paymentType,
			reserveTime = checkoutPrices.reserveTime,
			description = description,
			bag = bag
		)
	}

	fun orderNotice(id: Int) {
		repository.orderNotice(id, bag)
	}

	fun checkPayment() {
		mOrderAdd?.also {
			repository.checkOrderPayment(it.id, bag)
		}
	}

	fun setPaymentsAndReserve() {
		checkoutPrices.isOffline = shopCartTools.offline == 1
		checkoutPrices.isOnline = shopCartTools.online == 1
		checkoutPrices.isReserve = shopCartTools.reserve == 1
		checkoutPrices.isNow = shopCartTools.now == 1
		checkoutPrices.orderNow = shopCartTools.orderNow
		checkoutPrices.orderLater = shopCartTools.orderLater
		checkoutPrices.courier_price = shopCartTools.courier_price
		if (!checkoutPrices.isReserve && shopCartTools.now == 1) {
			checkoutPrices.orderTime = 1
		}

		if (!checkoutPrices.isOffline) {
			checkoutPrices.paymentType = 1
		}
		d(
			"checkoutPrices.isOffline  : ${checkoutPrices.isOffline}\n" +
					"checkoutPrices.isOnline  : ${checkoutPrices.isOnline}\n" +
					"checkoutPrices.isReserve  : ${checkoutPrices.isReserve}\n" +
					"checkoutPrices.isNow  : ${checkoutPrices.isNow}\n" +
					"checkoutPrices.orderNow  : ${checkoutPrices.orderNow}\n" +
					"checkoutPrices.orderLater  : ${checkoutPrices.orderLater}\n" +
					" checkoutPrices.orderTime  : ${checkoutPrices.orderTime}\n" +
					"checkoutPrices.paymentType  : ${checkoutPrices.paymentType}", "TaG"
		)
	}
}
