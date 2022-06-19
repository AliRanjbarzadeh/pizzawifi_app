package ir.atriatech.pizzawifi.entities.home.tournament


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class QuestionJson(
    @SerializedName("questionId")
    @Expose
    var questionId: Int = 0,
    @SerializedName("answerId")
    @Expose
    var answerId: Int = 0
)