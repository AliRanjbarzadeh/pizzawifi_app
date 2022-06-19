package ir.atriatech.pizzawifi.entities.profile.competition


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CompetitionQuestion(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = "",
    @SerializedName("user_select")
    @Expose
    var userSelect: Int = 0,
    @SerializedName("correct_answer")
    @Expose
    var correctAnswer: Int = 0,
    @SerializedName("competition_answers")
    @Expose
    var competitionAnswers: MutableList<CompetitionAnswer> = mutableListOf()
) {
    var index = 0

    fun getTitleWithIndex() = "$index- $title"
}