package ir.atriatech.pizzawifi.entities.shopcart


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StepJson(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("materials")
	@Expose
	var materials: MutableList<MaterialJson> = mutableListOf(),
	@SerializedName("left_materials")
	@Expose
	var leftMaterials: MutableList<MaterialJson> = mutableListOf(),
	@SerializedName("right_materials")
	@Expose
	var rightMaterials: MutableList<MaterialJson> = mutableListOf()
) {
	override fun toString(): String {
		return "StepJson(id=$id, materials=$materials, leftMaterials=$leftMaterials, rightMaterials=$rightMaterials)"
	}
}