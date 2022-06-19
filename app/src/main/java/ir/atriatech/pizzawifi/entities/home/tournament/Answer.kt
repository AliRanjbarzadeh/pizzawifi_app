package ir.atriatech.pizzawifi.entities.home.tournament

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = ""
) : BaseObservable(), Parcelable {


    var isSelected = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
    }

    override fun toString(): String {
        return "Answer(title='$title', isSelected='$isSelected')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Answer> = object : Parcelable.Creator<Answer> {
            override fun createFromParcel(source: Parcel): Answer = Answer(source)
            override fun newArray(size: Int): Array<Answer?> = arrayOfNulls(size)
        }
    }
}