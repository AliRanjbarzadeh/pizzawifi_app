package ir.atriatech.pizzawifi.entities.profile.address


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddressSearch(
    @SerializedName("id")
    @Expose
    var id: String = "",
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0,
    @SerializedName("lng")
    @Expose
    var lng: Double = 0.0,
    @SerializedName("name")
    @Expose
    var name: String = ""
)