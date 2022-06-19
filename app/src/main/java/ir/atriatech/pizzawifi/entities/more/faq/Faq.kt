package ir.atriatech.pizzawifi.entities.more.faq


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Faq(
    @SerializedName("question")
    @Expose
    var title: String = "",
    @SerializedName("answer")
    @Expose
    var value: String = "",
    var isExpanded: Boolean = false
)