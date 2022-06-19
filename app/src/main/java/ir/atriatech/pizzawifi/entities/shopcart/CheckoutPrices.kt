package ir.atriatech.pizzawifi.entities.shopcart

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.pizzawifi.common.AppObject

class CheckoutPrices : BaseObservable() {

	var orderSum: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.orderSum)
			notifyPropertyChanged(BR.orderSumFormat)
			notifyPropertyChanged(BR.totalToPayFormat)
			notifyPropertyChanged(BR.totalToPay)
		}

	var paymentType: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.paymentType)
		}

	val orderSumFormat: String
		@Bindable get() = orderSum.priceFormat()

	var tax: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.tax)
			notifyPropertyChanged(BR.taxFormat)
			notifyPropertyChanged(BR.totalToPayFormat)
			notifyPropertyChanged(BR.totalToPay)
		}

	val taxFormat: String
		@Bindable get() = tax.priceFormat()

	var toll: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.toll)
			notifyPropertyChanged(BR.tollFormat)
			notifyPropertyChanged(BR.totalToPayFormat)
			notifyPropertyChanged(BR.totalToPay)
		}

	val tollFormat: String
		@Bindable get() = toll.priceFormat()

	var courier_price: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.courier_price)
			notifyPropertyChanged(BR.courier_priceFormat)
			notifyPropertyChanged(BR.totalToPayFormat)
			notifyPropertyChanged(BR.totalToPay)
		}

	val wallet_priceFormat: String
		@Bindable get() = walletAmount.priceFormat()

	val courier_priceFormat: String
		@Bindable get() = if (courier_price > 0) courier_price.priceFormat() else "رایگان"


	var productDiscount: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.productDiscount)
			notifyPropertyChanged(BR.discountFormat)
			notifyPropertyChanged(BR.totalToPayFormat)
			notifyPropertyChanged(BR.totalToPay)
		}

	var discount: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.discount)
			notifyPropertyChanged(BR.discountFormat)
			notifyPropertyChanged(BR.totalToPayFormat)
			notifyPropertyChanged(BR.totalToPay)
		}

	val discountFormat: String
		@Bindable get() = (discount + productDiscount).priceFormat()

	val totalToPay: Int
		@Bindable get() = orderSum + tax + toll + courier_price - productDiscount - discount - walletAmount


	val totalToPayFormat: String
		@Bindable get() = (orderSum + tax + toll + courier_price - productDiscount - discount - walletAmount).priceFormat()

	var walletAmount: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.walletFormat)
			notifyPropertyChanged(BR.wallet_priceFormat)
		}

	val walletFormat: String
		@Bindable get() = (AppObject.userInfo.wallet - walletAmount).priceFormat()

	var isOnline: Boolean = false
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.online)
		}

	var isOffline: Boolean = false
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.offline)
		}


	var orderTime: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.orderTime)
		}

	var isReserve: Boolean = false
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.reserve)
			notifyPropertyChanged(BR.showOrderTime)
		}

	var isNow: Boolean = false
		@Bindable get() {
			if (!isReserve && orderTime == 1) {
				return false
			}
			return field
		}
		set(value) {
			field = value
			notifyPropertyChanged(BR.now)
			notifyPropertyChanged(BR.showOrderTime)
		}

	@Bindable
	fun isShowOrderTime(): Boolean {
		return isReserve || isNow;
	}

	var orderNow: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.orderNow)
		}

	var orderLater: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.orderLater)
		}

	var reserveTime: String = ""

	var discountCode: String = ""
	var discountPercent: Int = 0
	var discountMaxAmount: Int = 0

	fun reset() {
		orderSum = 0
		productDiscount = 0
		walletAmount = 0
	}
}