package ir.atriatech.pizzawifi.entities.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogLink {
    @SerializedName("app")
    @Expose
    var app: String = ""

    @SerializedName("web")
    @Expose
    var web: String = ""
}
