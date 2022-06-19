package ir.atriatech.core.entities


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Update(
    @SerializedName("update_link")
    @Expose
    var updateLink: String="",
    @SerializedName("update_message")
    @Expose
    var updateMessage: String="",
    @SerializedName("update_type")
    @Expose
    var updateType: Int=0
)