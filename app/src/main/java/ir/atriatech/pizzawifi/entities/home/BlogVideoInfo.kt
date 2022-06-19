package ir.atriatech.pizzawifi.entities.home


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlogVideoInfo(
    @SerializedName("category")
    @Expose
    var category: String,
    @SerializedName("duration")
    @Expose
    var duration: Int,
    @SerializedName("image")
    @Expose
    var image: String,
    @SerializedName("link")
    @Expose
    var link: String,
    @SerializedName("title")
    @Expose
    var title: String
) {
    fun getTime(): String {
        val min = duration / 60
        val second = duration - (min * 60)
        var minStr = "$min"
        var secondStr = "$second"
        if (min < 10) {
            minStr = "0${min}"
        }
        if (second < 10) {
            secondStr = "0${second}"
        }
        return "${minStr}:${secondStr}"
    }
}