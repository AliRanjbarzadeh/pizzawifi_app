package ir.atriatech.pizzawifi.ui.main.shopcart.offer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.pizzawifi.entities.Product

data class ShopCartOfferModel(
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("products")
    @Expose
    val products: MutableList<Product>
)