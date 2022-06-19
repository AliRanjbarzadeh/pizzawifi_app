package ir.atriatech.pizzawifi.entities.profile.competition


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Competition(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = "",
    @SerializedName("remain_time")
    @Expose
    var remainTime: Long = 0,
    @SerializedName("c_date")
    @Expose
    var cDate: String = ""
) : BaseObservable(), Parcelable {

    @SerializedName("is_end")
    @Expose
    var isEnd: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.end)
        }

    @SerializedName("questions")
    @Expose
    var questions = mutableListOf<CompetitionQuestion>()


    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readLong(),
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeLong(remainTime)
        writeString(cDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Competition> = object : Parcelable.Creator<Competition> {
            override fun createFromParcel(source: Parcel): Competition = Competition(source)
            override fun newArray(size: Int): Array<Competition?> = arrayOfNulls(size)
        }
    }
}