package ir.atriatech.pizzawifi.entities.profile.club

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat

data class ClubItemModel(
	@SerializedName("id")
	@Expose
	var id: Int = 0,

	@SerializedName("title")
	@Expose
	var name: String = "",

	@SerializedName("description")
	@Expose
	var description: String = "",

	@SerializedName("type")
	@Expose
	var type: String = "",

	@SerializedName("package_type")
	@Expose
	var packageType: ClubPackageTypeModel? = null,

	@SerializedName("club_rate")
	@Expose
	var rate: Int = 0,

	@SerializedName("image")
	@Expose
	var image: String = ""
) : Parcelable {

	fun getRateFormat(): String {
		return rate.priceFormat("")
	}

	constructor(source: Parcel) : this(
		source.readInt(),
		source.readString() ?: "",
		source.readString() ?: "",
		source.readString() ?: "",
		source.readParcelable<ClubPackageTypeModel>(ClubPackageTypeModel::class.java.classLoader),
		source.readInt(),
		source.readString() ?: "",
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeString(name)
		writeString(description)
		writeString(type)
		writeParcelable(packageType, flags)
		writeInt(rate)
		writeString(image)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<ClubItemModel> = object : Parcelable.Creator<ClubItemModel> {
			override fun createFromParcel(source: Parcel): ClubItemModel = ClubItemModel(source)
			override fun newArray(size: Int): Array<ClubItemModel?> = arrayOfNulls(size)
		}
	}

	override fun toString(): String {
		return "ClubItemModel(name='$name', rate=$rate, rateFormat='${getRateFormat()}')"
	}
}