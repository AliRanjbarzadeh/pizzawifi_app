package ir.atriatech.pizzawifi.entities


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProvinceModel(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("cities")
    @Expose
    var cities: List<CityModel> = listOf()

) : BaseObservable(), Parcelable {

    var selected: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        ArrayList<CityModel>().apply {
            source.readList(
                this as List<*>,
                CityModel::class.java.classLoader
            )
        }

    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeList(cities)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProvinceModel> =
            object : Parcelable.Creator<ProvinceModel> {
                override fun createFromParcel(source: Parcel): ProvinceModel = ProvinceModel(source)
                override fun newArray(size: Int): Array<ProvinceModel?> = arrayOfNulls(size)
            }
    }
}