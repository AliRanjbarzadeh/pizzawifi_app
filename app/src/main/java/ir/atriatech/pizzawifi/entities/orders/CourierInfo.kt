package ir.atriatech.pizzawifi.entities.orders


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CourierInfo(
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0,
    @SerializedName("lng")
    @Expose
    var lng: Double = 0.0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("mobile")
    @Expose
    var mobile: String = ""
)