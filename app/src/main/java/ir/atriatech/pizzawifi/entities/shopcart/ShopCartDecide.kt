package ir.atriatech.pizzawifi.entities.shopcart


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.pizzawifi.entities.Branch

data class ShopCartDecide(
    @SerializedName("branch")
    @Expose
    var branch: Branch = Branch(),

    @SerializedName("is_branch")
    @Expose
    var is_branch: Int = 0,

    @SerializedName("eat_in_place")
    @Expose
    var eatInPlace: Int = 0,

    @SerializedName("take_by_self")
    @Expose
    var takeBySelf: Int = 0,

    @SerializedName("send_to_me")
    @Expose
    var sendToMe: Int = 0
)