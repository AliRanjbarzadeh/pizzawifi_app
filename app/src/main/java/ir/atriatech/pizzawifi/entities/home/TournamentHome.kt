package ir.atriatech.pizzawifi.entities.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TournamentHome(
    @SerializedName("is_active")
    @Expose
    val isActive: Int = 0,

    @SerializedName("title")
    @Expose
    val title: String = "",

    @SerializedName("link")
    @Expose
    val link: String = "",

    @SerializedName("description")
    @Expose
    val description: String = "",

    @SerializedName("already")
    @Expose
    var already: Int = 0,

    @SerializedName("already_msg")
    @Expose
    val alreadyMsg: String = ""
)