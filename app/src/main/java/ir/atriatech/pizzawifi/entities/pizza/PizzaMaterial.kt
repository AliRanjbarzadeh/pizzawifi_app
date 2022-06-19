package ir.atriatech.pizzawifi.entities.pizza


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PizzaMaterial(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("image")
    @Expose
    var image: String = ""
) : BaseObservable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeString(image)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PizzaMaterial> =
            object : Parcelable.Creator<PizzaMaterial> {
                override fun createFromParcel(source: Parcel): PizzaMaterial = PizzaMaterial(source)
                override fun newArray(size: Int): Array<PizzaMaterial?> = arrayOfNulls(size)
            }
    }
}