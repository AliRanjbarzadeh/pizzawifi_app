package ir.atriatech.pizzawifi.entities.shopcart


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BreadJson(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("material_id")
    @Expose
    var materialId: Int = 0,
    @SerializedName("steps")
    @Expose
    var steps: MutableList<StepJson> = mutableListOf()
)