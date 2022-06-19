package ir.atriatech.pizzawifi.entities


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeneralModel(
    @SerializedName("description")
    @Expose
    var description: String = ""
)