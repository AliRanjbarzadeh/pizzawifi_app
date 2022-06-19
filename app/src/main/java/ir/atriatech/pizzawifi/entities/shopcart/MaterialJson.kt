package ir.atriatech.pizzawifi.entities.shopcart


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MaterialJson(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("material_id")
    @Expose
    var materialId: Int = 0,
    @SerializedName("qty")
    @Expose
    var qty: Int = 0
) {
    override fun toString(): String {
        return "MaterialJson(id=$id, materialId=$materialId, qty=$qty)"
    }
}