package ir.atriatech.pizzawifi.entities.profile.support


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Support(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = "",
    @SerializedName("content")
    @Expose
    var message: String = "",
    @SerializedName("c_date")
    @Expose
    var cDate: String = ""
) : BaseObservable(), Parcelable {
    @SerializedName("status")
    @Expose
    var status: Int = 0
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.status)
        }

    @SerializedName("status_text")
    @Expose
    var statusText: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.statusText)
        }

    @SerializedName("is_admin")
    @Expose
    var isAdmin: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.admin)
        }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeString(message)
        writeString(cDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Support> = object : Parcelable.Creator<Support> {
            override fun createFromParcel(source: Parcel): Support = Support(source)
            override fun newArray(size: Int): Array<Support?> = arrayOfNulls(size)
        }
    }
}