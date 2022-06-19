package ir.atriatech.pizzawifi.entities.home


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlogVideo(
	@SerializedName("id")
	@Expose
	var id: String,
	@SerializedName("web_link")
	@Expose
	var link: String,
	@SerializedName("info")
	@Expose
	var blogVideoInfo: BlogVideoInfo
)