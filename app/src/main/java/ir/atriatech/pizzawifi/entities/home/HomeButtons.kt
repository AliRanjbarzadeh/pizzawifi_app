package ir.atriatech.pizzawifi.entities.home

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HomeButtons(
	@SerializedName("maker_active")
	@Expose
	var makerActive: Int = 0,

	@SerializedName("maker_title")
	@Expose
	var makerTitle: String = "",

	@SerializedName("maker_description")
	@Expose
	var makerDescription: String = "",

	@SerializedName("menu_active")
	@Expose
	var menuActive: Int = 0,

	@SerializedName("menu_title")
	@Expose
	var menuTitle: String = "",

	@SerializedName("customer_active")
	@Expose
	var customerActive: Int = 0,

	@SerializedName("customer_title")
	@Expose
	var customerTitle: String = ""
) : BaseObservable() {
	var cityButtonText: String = ""
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.cityButtonText)
		}
}
