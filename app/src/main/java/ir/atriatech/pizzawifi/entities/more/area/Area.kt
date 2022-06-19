package ir.atriatech.pizzawifi.entities.more.area


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Area(
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0,
    @SerializedName("lng")
    @Expose
    var lng: Double = 0.0
)