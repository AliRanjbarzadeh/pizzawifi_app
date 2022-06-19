package ir.atriatech.pizzawifi.entities.profile.competition


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CompetitionAnswer(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = ""
) {
    var isUserAnswer = false
}