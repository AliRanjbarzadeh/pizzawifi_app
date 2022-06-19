package ir.atriatech.pizzawifi.entities.shopcart


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShopCartTools(
    @SerializedName("is_online")
    @Expose
    var online: Int = 0,
    @SerializedName("is_offline")
    @Expose
    var offline: Int = 0,
    @SerializedName("order_now")
    @Expose
    var orderNow: String = "",
    @SerializedName("order_later")
    @Expose
    var orderLater: String = "",
    @SerializedName("is_now")
    @Expose
    var now: Int = 0,
    @SerializedName("is_reserve")
    @Expose
    var reserve: Int = 0,

    @SerializedName("courier_price")
    @Expose
    var courier_price: Int = 0,

    @SerializedName("reserve_times")
    @Expose
    var reserveTimes: List<ReserveTime> = listOf(),

    @SerializedName("wallet")
    @Expose
    var wallet: Int = 0
)