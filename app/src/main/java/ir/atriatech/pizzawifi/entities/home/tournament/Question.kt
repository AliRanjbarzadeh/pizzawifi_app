package ir.atriatech.pizzawifi.entities.home.tournament


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("title")
    @Expose
    var title: String = "",
    @SerializedName("answer_time")
    @Expose
    var answerTime: Long = 0,
    @SerializedName("answers")
    @Expose
    var answers: List<Answer> = listOf()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readLong(),
        ArrayList<Answer>().apply {
            source.readList(
                this as List<*>,
                Answer::class.java.classLoader
            )
        }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeLong(answerTime)
        writeTypedList(answers)
    }

    override fun toString(): String {
        return "Question(title='$title', answers=$answers)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Question> = object : Parcelable.Creator<Question> {
            override fun createFromParcel(source: Parcel): Question = Question(source)
            override fun newArray(size: Int): Array<Question?> = arrayOfNulls(size)
        }
    }


}