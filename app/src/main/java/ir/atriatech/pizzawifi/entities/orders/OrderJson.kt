package ir.atriatech.pizzawifi.entities.orders


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderJson(
    @SerializedName("products")
    @Expose
    var products: MutableList<OrderItemJson>,
    @SerializedName("foods")
    @Expose
    var pizzas: MutableList<OrderItemJson>
)