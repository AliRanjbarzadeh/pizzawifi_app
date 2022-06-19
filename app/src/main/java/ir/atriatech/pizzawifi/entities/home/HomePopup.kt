package ir.atriatech.pizzawifi.entities.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HomePopup(
    @SerializedName("type")
    @Expose
    var type: String? = "",
    @SerializedName("image")
    @Expose
    var image: String? = "",
    @SerializedName("description")
    @Expose
    var description: String? = "",
    @SerializedName("object_id")
    @Expose
    var objectId: Int? = 0,
    @SerializedName("object_type")
    @Expose
    var objectType: String? = "",
    @SerializedName("can_go")
    @Expose
    var canGo: Boolean = true
)