package ir.atriatech.pizzawifi.entities


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.app.findString
import ir.atriatech.pizzawifi.R

data class Branch(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("name")
	@Expose
	var name: String = "",
	@SerializedName("address")
	@Expose
	var address: String = "",
	@SerializedName("distance")
	@Expose
	var distance: Int = -1, /*  if distance == -1 don't show distance in branch item in list  */

	var status: Int = 0

) : BaseObservable(), Parcelable {

	@SerializedName("city_id")
	@Expose
	var cityId: Int = 0

	@SerializedName("phone")
	@Expose
	var phone: String = ""

	@SerializedName("lat")
	@Expose
	var lat: Double = 0.0

	@SerializedName("lng")
	@Expose
	var lng: Double = 0.0

	@Bindable
	fun getNameWithSuffix(): String {
		if (isSelected) {
			return String.format(findString(R.string.yourSelectedBranch), name)
		}
		return name
	}

	var isSelected = status == 1
		@Bindable get() = status == 1
		set(value) {
			field = value
			status = if (value) {
				1
			} else {
				0
			}

			notifyPropertyChanged(BR.selected)
			notifyPropertyChanged(BR.nameWithSuffix)
		}

	constructor(source: Parcel) : this(
		source.readInt(),
		source.readString() ?: "",
		source.readString() ?: "",
		source.readInt(),
		source.readInt()

	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeString(name)
		writeString(address)
		writeInt(distance)
		writeInt(status)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Branch> = object : Parcelable.Creator<Branch> {
			override fun createFromParcel(source: Parcel): Branch = Branch(source)
			override fun newArray(size: Int): Array<Branch?> = arrayOfNulls(size)
		}
	}
}