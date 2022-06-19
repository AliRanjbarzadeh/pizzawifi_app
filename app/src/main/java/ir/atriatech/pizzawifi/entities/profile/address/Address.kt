package ir.atriatech.pizzawifi.entities.profile.address


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("mobile")
    @Expose
    var mobile: String = "",
    @SerializedName("address")
    @Expose
    var address: String = "",
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0,
    @SerializedName("lng")
    @Expose
    var lng: Double = 0.0
) : BaseObservable(), Parcelable {
	var isSelected: Boolean = false
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.selected)
		}

	constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readDouble(),
        source.readDouble()
    )

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeString(name)
		writeString(mobile)
		writeString(address)
		writeDouble(lat)
		writeDouble(lng)
	}

	fun update(mItem: Address) {
		this.name = mItem.name
		this.mobile = mItem.mobile
		this.address = mItem.address
		this.lat = mItem.lat
		this.lng = mItem.lng
		notifyChange()
	}

	override fun toString(): String {
		return "Address(id=$id, lat=$lat, lng=$lng)"
	}


	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Address> = object : Parcelable.Creator<Address> {
			override fun createFromParcel(source: Parcel): Address = Address(source)
			override fun newArray(size: Int): Array<Address?> = arrayOfNulls(size)
		}
	}
}