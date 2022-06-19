package ir.atriatech.pizzawifi.entities.profile.club

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClubPackageTypeModel(
	@SerializedName("type")
	@Expose
	var type: String = "",
	@SerializedName("name")
	@Expose
	var name: String = ""
) : Parcelable {
	constructor(source: Parcel) : this(
		source.readString() ?: "",
		source.readString() ?: "",
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(type)
		writeString(name)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<ClubPackageTypeModel> = object : Parcelable.Creator<ClubPackageTypeModel> {
			override fun createFromParcel(source: Parcel): ClubPackageTypeModel = ClubPackageTypeModel(source)
			override fun newArray(size: Int): Array<ClubPackageTypeModel?> = arrayOfNulls(size)
		}
	}
}