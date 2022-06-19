package ir.atriatech.pizzawifi.entities.profile.campaign


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Campaign(
	@SerializedName("campaign_name")
	@Expose
	val campaignName: String="",
	@SerializedName("created_at")
	@Expose
	val createdAt: String="",
	@SerializedName("decline_reason")
	@Expose
	val declineReason: String?="",
	@SerializedName("image")
	@Expose
	val image: String="",
	@SerializedName("status")
	@Expose
	val status: String="",
	@SerializedName("status_color")
	@Expose
	val statusColor: String="#00",
	@SerializedName("status_text")
	@Expose
	val statusText: String=""
) :Parcelable