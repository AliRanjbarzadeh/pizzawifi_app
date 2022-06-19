package ir.atriatech.pizzawifi.entities.customerMenu


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("comment")
    @Expose
    var comment: String = "",
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("rate")
    @Expose
    var rate: Float = 0f
)