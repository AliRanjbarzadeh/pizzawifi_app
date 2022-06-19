package ir.atriatech.core.entities


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomLocation(
    @SerializedName("lat")
    @Expose
    var lat: Double=0.0,
    @SerializedName("lng")
    @Expose
    var lng: Double=0.0,
    @SerializedName("title")
    @Expose
    var title: String=""
)