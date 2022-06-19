package ir.atriatech.core.entities


import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName

data class SimpleItem(
    var id: Int=0,
    var name: String="",
    @DrawableRes val drawable: Int,
    val tag: String
)