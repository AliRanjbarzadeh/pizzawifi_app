package ir.atriatech.pizzawifi.entities.home.maker


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Material(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("name")
	@Expose
	var name: String = "",
	@SerializedName("material_id")
	@Expose
	var materialId: Int = 0,
	@SerializedName("step_id")
	@Expose
	var stepId: Int = 0,
	@SerializedName("type")
	@Expose
	var type: Int = 0,
	@SerializedName("icon")
	@Expose
	var icon: String = "",
	@SerializedName("plate")
	@Expose
	var plate: String = "",
	@SerializedName("price")
	@Expose
	var price: Int = 0,
	@SerializedName("quantity")
	@Expose
	var quantity: Int = 0,
	@SerializedName("calory")
	@Expose
	var calory: Int = 0,
	@SerializedName("plate2")
	@Expose
	var plate2: String = "",
	@SerializedName("price2")
	@Expose
	var price2: Int = 0,
	@SerializedName("quantity2")
	@Expose
	var quantity2: Int = 0,
	@SerializedName("calory2")
	@Expose
	var calory2: Int = 0,
	@SerializedName("plate3")
	@Expose
	var plate3: String = "",
	@SerializedName("price3")
	@Expose
	var price3: Int = 0,
	@SerializedName("quantity3")
	@Expose
	var quantity3: Int = 0,
	@SerializedName("calory3")
	@Expose
	var calory3: Int = 0,
	@SerializedName("entity")
	@Expose
	var entity: Int = 0,
	@SerializedName("prices")
	@Expose
	var prices: HashMap<String, MaterialPrice>? = HashMap()
) : BaseObservable(), Parcelable {
	var qty: Int = 0
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.qty)
			notifyPropertyChanged(BR.qtyName)
		}

	var isAlpha: Boolean = false
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.alpha)
		}

	@Bindable
	fun getQtyName(): String {
		return when {
			type == 2 && qty == 1 -> "کم"
			type == 2 && qty == 2 -> "زیاد"
			type == 3 && qty == 1 -> "کم"
			type == 3 && qty == 2 -> "متوسط"
			type == 3 && qty == 3 -> "زیاد"

			else -> ""
		}
	}

	fun reset() {
		qty = 0
		isAlpha = false
	}

	constructor(source: Parcel) : this(
		source.readInt(),
		source.readString() ?: "",
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readString() ?: "",
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readSerializable() as HashMap<String, MaterialPrice>
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeString(name)
		writeInt(materialId)
		writeInt(stepId)
		writeInt(type)
		writeString(icon)
		writeString(plate)
		writeInt(price)
		writeInt(quantity)
		writeInt(calory)
		writeString(plate2)
		writeInt(price2)
		writeInt(quantity2)
		writeInt(calory2)
		writeString(plate3)
		writeInt(price3)
		writeInt(quantity3)
		writeInt(calory3)
		writeInt(entity)
		writeSerializable(prices)
	}

	override fun toString(): String {
		return "Material(id=$id, name='$name', qty=$qty, prices=$prices)"
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Material> = object : Parcelable.Creator<Material> {
			override fun createFromParcel(source: Parcel): Material = Material(source)
			override fun newArray(size: Int): Array<Material?> = arrayOfNulls(size)
		}
	}
}