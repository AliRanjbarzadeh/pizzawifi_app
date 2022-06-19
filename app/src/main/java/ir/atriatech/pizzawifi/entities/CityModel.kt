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

data class CityModel(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("name")
	@Expose
	var name: String = "",

	var status: Int = 0
) : BaseObservable(), Parcelable {
	@SerializedName("lat")
	@Expose
	var lat: Double = 0.0

	@SerializedName("lng")
	@Expose
	var lng: Double = 0.0

	@Bindable
	fun getNameWithSuffix(): String {
		if (isSelected) {
			return String.format(findString(R.string.yourSelectedCity), name)
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

	@SerializedName("branches")
	@Expose
	var branches: MutableList<Branch> = mutableListOf()



	constructor(source: Parcel) : this(
		source.readInt(),
		source.readString() ?: "",
		source.readInt()

	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeString(name)
		writeInt(status)
	}

	override fun toString(): String {
		return "CityModel(id=$id, name='$name', status=$status, isSelected=$isSelected)"
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<CityModel> = object : Parcelable.Creator<CityModel> {
			override fun createFromParcel(source: Parcel): CityModel = CityModel(source)
			override fun newArray(size: Int): Array<CityModel?> = arrayOfNulls(size)
		}
	}
}