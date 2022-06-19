package ir.atriatech.pizzawifi.entities.orders.survey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SurveyJson(
    @SerializedName("questionId")
    @Expose
    private val questionId: Int,

    @SerializedName("rate")
    @Expose
    private val rate: Float
)
