package ir.atriatech.pizzawifi.entities.profile.lottery

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LotteryModel(
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("description")
    @Expose
    var description: String = "",
    @SerializedName("participant_date")
    @Expose
    var participant_date: String = "",
    @SerializedName("end_date")
    @Expose
    var end_date: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(description)
        writeString(participant_date)
        writeString(end_date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LotteryModel> = object : Parcelable.Creator<LotteryModel> {
            override fun createFromParcel(source: Parcel): LotteryModel = LotteryModel(source)
            override fun newArray(size: Int): Array<LotteryModel?> = arrayOfNulls(size)
        }
    }
}