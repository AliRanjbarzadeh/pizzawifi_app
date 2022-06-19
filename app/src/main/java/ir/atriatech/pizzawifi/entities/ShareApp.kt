package ir.atriatech.pizzawifi.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShareApp(
    @SerializedName("text")
    @Expose
    var text: String = "",
    @SerializedName("content")
    @Expose
    var content: String = ""
)
