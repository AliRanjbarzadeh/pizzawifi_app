package ir.atriatech.pizzawifi.ui.main.more.requestForm

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName

class SituationModel() : BaseObservable() {

    @SerializedName("name")
    var name = ""

    var selected: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    constructor(newName: String, selected: Boolean) : this() {
        this.name = newName
        this.selected = selected
    }

    override fun toString(): String {
        return "GameType(name='$name', selected ='$selected')"
    }

}