package ir.atriatech.pizzawifi.entities.home.maker

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MaterialPrice(
	@SerializedName("price")
	@Expose
	var price: Int = 0,
	@SerializedName("quantity")
	@Expose
	var quantity: Int = 0
) : Parcelable