package ir.atriatech.pizzawifi.entities.orders


import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.CityModel

open class Order(
	@SerializedName("discount")
	@Expose
	var discount: Int = 0,
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("order_time")
	@Expose
	var orderTime: String = "",
	@SerializedName("order_sum")
	@Expose
	var orderSum: Int = 0,
	@SerializedName("wait_time")
	@Expose
	var waitTime: Long = 0,
	@SerializedName("tax")
	@Expose
	var tax: Int = 0,
	@SerializedName("courier_price")
	@Expose
	var courier_price: Int = 0,
	@SerializedName("toll")
	@Expose
	var toll: Int = 0,
	@SerializedName("total_cost")
	@Expose
	var totalCost: Int = 0,
	@SerializedName("total_remain")
	@Expose
	var totalRemain: Int = 0,
	@SerializedName("wallet")
	@Expose
	var wallet: Int = 0,
	@SerializedName("delivery_type") // 1: I take - 2: Eat in - 3: Send to me
	@Expose
	var deliveryType: Int = 0,
	@SerializedName("name")
	@Expose
	var name: String = "",
	@SerializedName("mobile")
	@Expose
	var mobile: String = "",
	@SerializedName("address")
	@Expose
	var address: String = "",
	@SerializedName("order_status") // 0: Pending - 1: Accepted - 2: Sent - 3: Delivered - 4: Declined
	@Expose
	var orderStatus: Int = 0,
	@SerializedName("is_reorder")
	@Expose
	var isReorder: Boolean = false
) : Parcelable {
	@SerializedName("order_items")
	@Expose
	var orderItems: JsonElement? = null

	@SerializedName("survey")
	@Expose
	var survey: Int = 0

	@SerializedName("can_survey")
	@Expose
	var isCanSurvey: Boolean = false

	@SerializedName("status_text")
	@Expose
	var statusText: String = ""

	@SerializedName("decline_reason")
	@Expose
	var declineReason: String = ""

	@SerializedName("is_timer")
	@Expose
	var isTimer: Boolean = false

	@SerializedName("branch")
	@Expose
	var branch: Branch = Branch()

	@SerializedName("city")
	@Expose
	var city: CityModel = CityModel()

	@SerializedName("show_courier")
	@Expose
	var showCourier: Int = 0

	@SerializedName("courier_info")
	@Expose
	var courierInfo: CourierInfo = CourierInfo()

	val mapVisibility: Int
		get() {
			if (showCourier == 1 && courierInfo.name.isNotEmpty() && orderStatus == 2)
				return View.VISIBLE
			return View.GONE
		}

	protected constructor(order: Order) : this(
		id = order.id,
		orderStatus = order.orderStatus,
		waitTime = order.waitTime
	)

	fun getFormatTotal() = (totalRemain + totalCost).priceFormat()

	fun getFormatTotalPay(): String = (totalRemain + totalCost).priceFormat()
//		spannableString(
//			firstString = "مبلغ کل: ",
//			firstColor = R.color.black,
//			secondString = (totalRemain + totalCost).priceFormat(),
//			secondColor = R.color.black
//		)

	fun getOrderSumFormat(): String = orderSum.priceFormat()
//		spannableString(
//			firstString = "مبلغ سفارش: ",
//			firstColor = R.color.black,
//			secondString = orderSum.priceFormat(),
//			secondColor = R.color.black
//		)

	fun getTaxFormat(): String = tax.priceFormat()
//		spannableString(
//		firstString = "مالیات: ",
//		firstColor = R.color.black,
//		secondString = tax.priceFormat(),
//		secondColor = R.color.black
//	)

	fun getTollFormat(): String = toll.priceFormat()
//		spannableString(
//			firstString = "عوارض: ",
//			firstColor = R.color.black,
//			secondString = toll.priceFormat(),
//			secondColor = R.color.black
//		)

	fun getDiscountFormat(): String = discount.priceFormat()
//		spannableString(
//			firstString = "تخفیف: ",
//			firstColor = R.color.black,
//			secondString = discount.priceFormat(),
//			secondColor = R.color.black
//		)

	fun getCourierPriceFormat(): String = courier_price.priceFormat()
//		spannableString(
//			firstString = "هزینه ارسال: ",
//			firstColor = R.color.black,
//			secondString = courier_price.priceFormat(),
//			secondColor = R.color.black
//		)

	fun getWalletFormat(): String = wallet.priceFormat()
//		spannableString(
//			firstString = "کیف پول: ",
//			firstColor = R.color.black,
//			secondString = wallet.priceFormat(),
//			secondColor = R.color.black
//		)

	fun getTotalPriceFormat(): String = (totalRemain + totalCost).priceFormat()
//		spannableString(
//			firstString = "مبلغ قابل پرداخت: ",
//			firstColor = R.color.black,
//			secondString = (totalRemain + totalCost).priceFormat(),
//			secondColor = R.color.black
//		)

	fun getTotalRemainFormat(): String = totalRemain.priceFormat()

	fun getTotalCostFormat(): String = totalCost.priceFormat()

	fun getFormatBranchName(): String = branch.name
//		spannableString(
//			firstString = "نام شعبه: ",
//			firstColor = R.color.black,
//			secondString = branch.name,
//			secondColor = R.color.black
//		)

	fun isShowAllButtons(): Boolean {
		return when (orderStatus) {
			0, 1, 2 -> false

			else -> true
		}
	}

	override fun toString(): String {
		return "Order(id=$id, isReorder=$isReorder, statusText='$statusText', branch=$branch, city=$city)"
	}

	constructor(source: Parcel) : this(
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readInt(),
		source.readLong(),
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readString() ?: "",
		source.readString() ?: "",
		source.readInt(),
		1 == source.readInt()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(discount)
		writeInt(id)
		writeString(orderTime)
		writeInt(orderSum)
		writeLong(waitTime)
		writeInt(tax)
		writeInt(courier_price)
		writeInt(toll)
		writeInt(totalCost)
		writeInt(totalRemain)
		writeInt(wallet)
		writeInt(deliveryType)
		writeString(name)
		writeString(mobile)
		writeString(address)
		writeInt(orderStatus)
		writeInt((if (isReorder) 1 else 0))
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Order> = object : Parcelable.Creator<Order> {
			override fun createFromParcel(source: Parcel): Order = Order(source)
			override fun newArray(size: Int): Array<Order?> = arrayOfNulls(size)
		}
	}
}