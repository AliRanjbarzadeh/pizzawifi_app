package ir.atriatech.pizzawifi.entities.more.contactus

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyLocation(
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0,
    @SerializedName("lng")
    @Expose
    var lng: Double = 0.0,
    @SerializedName("title")
    @Expose
    var title: String = "",
    @SerializedName("show_map")
    @Expose
    var showMap: Int = 1
)