package ir.atriatech.pizzawifi.entities.profile.wallet


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat

data class Wallet(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("amount")
    @Expose
    var amount: Int = 0,
    @SerializedName("payment_type")
    @Expose
    var type: Int = 0,
    @SerializedName("description")
    @Expose
    var description: String = "",
    @SerializedName("c_date")
    @Expose
    var cDate: String = ""
) {
    //use only for wallet checkout payment
    @SerializedName("payment_status")
    @Expose
    var status: Int = 0

    fun getAmountFormat() = amount.priceFormat()
}