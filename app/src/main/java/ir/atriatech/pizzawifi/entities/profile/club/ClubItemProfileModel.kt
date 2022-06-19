package ir.atriatech.pizzawifi.entities.profile.club

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClubItemProfileModel(
	@SerializedName("title")
	@Expose
	var name: String = "",

	@SerializedName("description")
	@Expose
	var description: String = "",

	@SerializedName("package_type")
	@Expose
	var packageType: String = "",

	@SerializedName("gift_type")
	@Expose
	var giftType: String = "",

	@SerializedName("gift_value")
	@Expose
	var giftValue: JsonObject? = null,

	@SerializedName("image")
	@Expose
	var image: String = ""
) {
	override fun toString(): String {
		return "ClubItemProfileModel(packageType='$packageType', giftType='$giftType', giftValue=$giftValue)"
	}
}