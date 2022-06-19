package ir.atriatech.pizzawifi.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppUpdate(
    @SerializedName("update_type")
    @Expose
    var updateType: Int = 0,
    @SerializedName("update_message")
    @Expose
    var updateMessage: String = "",
    @SerializedName("update_link")
    @Expose
    var updateLink: String = ""
) {
    override fun toString(): String {
        return "AppUpdate(updateType=$updateType, updateMessage='$updateMessage', updateLink='$updateLink')"
    }
}
