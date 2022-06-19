package ir.atriatech.pizzawifi.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HomeExtra(
    @SerializedName("has_offer")
    @Expose
    var has_offer: Boolean = false,
    @SerializedName("tax")
    @Expose
    var tax: Int = 0,
    @SerializedName("toll")
    @Expose
    var toll: Int = 0,
    @SerializedName("update")
    @Expose
    var update: AppUpdate = AppUpdate(),
    @SerializedName("user_info")
    @Expose
    var user: UserInfo = UserInfo(),
    @SerializedName("share")
    @Expose
    var share: ShareApp = ShareApp(),
    @SerializedName("go_address")
    @Expose
    var goAddress: Int = 0,
    @SerializedName("show_price")
    @Expose
    var show_price: Int = 0
)
