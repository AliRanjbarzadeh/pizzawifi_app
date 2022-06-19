package ir.atriatech.pizzawifi.entities.more.area


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AreaItem(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("city_id")
	@Expose
	var cityId: Int = 0,
	@SerializedName("name")
	@Expose
	var title: String = "",
	@SerializedName("lat")
	@Expose
	var lat: Double = 0.0,
	@SerializedName("lng")
	@Expose
	var lng: Double = 0.0,
	@SerializedName("stroke_color")
	@Expose
	var strokeColor: String = "",
	@SerializedName("inner_color")
	@Expose
	var innerColor: String = "",
	@SerializedName("area")
	@Expose
	var area: MutableList<Area> = mutableListOf()
)