package ir.atriatech.pizzawifi.entities.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogCategory {
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("color")
    @Expose
    var color: String = ""

    @SerializedName("blogs")
    @Expose
    var blogs: MutableList<Blog> = mutableListOf()
}
