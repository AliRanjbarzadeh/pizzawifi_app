package ir.atriatech.pizzawifi.entities.orders


import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat

data class OrderItem(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("discount")
    @Expose
    var discount: Int = 0,
    @SerializedName("discount_percent")
    @Expose
    var discount_percent: Int = 0,
    @SerializedName("image")
    @Expose
    var image: String = "",
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("price")
    @Expose
    var price: Int = 0,
    @SerializedName("qty")
    @Expose
    var qty: Int = 0,
    @SerializedName("update_discount")
    @Expose
    var updateDiscount: Int = 0,
    @SerializedName("update_price")
    @Expose
    var updatePrice: Int = 0,
    @SerializedName("materials")
    @Expose
    var materials: JsonElement? = null
) {
    fun getFormatPrice() = price.priceFormat()

    @SerializedName("type")
    @Expose
    var type: Int = 0

    @SerializedName("max_choice")
    @Expose
    var max_choice: Int = 0
}