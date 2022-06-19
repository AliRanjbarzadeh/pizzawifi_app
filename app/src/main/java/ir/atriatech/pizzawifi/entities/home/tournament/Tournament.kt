package ir.atriatech.pizzawifi.entities.home.tournament


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Tournament(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = "",
    @SerializedName("end_time")
    @Expose
    var endTime: Int = 0,
    @SerializedName("questions")
    @Expose
    var questions: MutableList<Question> = mutableListOf()


) {
    override fun toString(): String {
        return "Tournament(questions=$questions)"
    }
}