package ir.atriatech.pizzawifi.entities.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.pizzawifi.entities.Product

data class HomeCategory(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("products")
    @Expose
    var products: MutableList<Product> = mutableListOf()
)
