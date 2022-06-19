package ir.atriatech.pizzawifi.entities.orders.survey

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Question : BaseObservable() {
    @SerializedName("id")
    @Expose
    val id: Int = 0

    @SerializedName("question")
    @Expose
    val question: String = ""

    var rating: Float = 3f
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.rating)
        }

    var description: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
        }
}