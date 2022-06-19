package ir.atriatech.pizzawifi.entities.orders


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderItemJson(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("qty")
    @Expose
    var qty: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("type")
    @Expose
    var type: Int = 0
) {
    @SerializedName("materials")
    @Expose
    var materials: Any? = null
}