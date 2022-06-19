package ir.atriatech.pizzawifi.entities.home.maker


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Bread(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("name")
	@Expose
	var name: String = "",
	@SerializedName("material_id")
	@Expose
	var materialId: Int = 0,
	@SerializedName("price")
	@Expose
	var price: Int = 0,
	@SerializedName("calory")
	@Expose
	var calory: Int = 0,
	@SerializedName("diameter")
	@Expose
	var diameter: String = "",
	@SerializedName("icon")
	@Expose
	var icon: String = "",
	@SerializedName("plate")
	@Expose
	var plate: String = "",
	@SerializedName("entity")
	@Expose
	var entity: Int = 0,
	@SerializedName("steps")
	@Expose
	var steps: List<Step> = listOf()
) : BaseObservable(), Parcelable {
	@SerializedName("diameter_size")
	@Expose
	var diameterSize: Int = 0

	var isSelected = false
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.selected)
		}

	var isAlpha = false
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.alpha)
		}

	fun reset() {
		isSelected = false
		isAlpha = false
		for (i in 0 until steps.size) {
			steps[i].reset()
		}
	}

	constructor(source: Parcel) : this(
		source.readInt(),
		source.readString() ?: "",
		source.readInt(),
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readString() ?: "",
		source.readString() ?: "",
		source.readInt(),
		ArrayList<Step>().apply { source.readList(this as List<*>, Step::class.java.classLoader) }
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeString(name)
		writeInt(materialId)
		writeInt(price)
		writeInt(calory)
		writeString(diameter)
		writeString(icon)
		writeString(plate)
		writeInt(entity)
		writeList(steps)
	}

	override fun toString(): String {
		return "Bread(id=$id, name='$name', price=$price, steps=$steps)"
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Bread> = object : Parcelable.Creator<Bread> {
			override fun createFromParcel(source: Parcel): Bread = Bread(source)
			override fun newArray(size: Int): Array<Bread?> = arrayOfNulls(size)
		}
	}


}