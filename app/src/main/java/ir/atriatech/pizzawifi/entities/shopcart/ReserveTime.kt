package ir.atriatech.pizzawifi.entities.shopcart


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReserveTime(
    @SerializedName("r_time")
    @Expose
    var rTime: String = ""
) : BaseObservable() {
    var isSelected: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}