package ir.atriatech.pizzawifi.entities.home.maker


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Step(
	@SerializedName("id")
	@Expose
	var id: Int = 0,
	@SerializedName("bread_id")
	@Expose
	var breadId: Int = 0,
	@SerializedName("name")
	@Expose
	var name: String = "",
	@SerializedName("description")
	@Expose
	var description: String = "",
	@SerializedName("min_choice")
	@Expose
	var minChoice: Int = 0,
	@SerializedName("max_choice")
	@Expose
	var maxChoice: Int = 0,
	@SerializedName("percent")
	@Expose
	var percent: List<Int> = listOf(),
	@SerializedName("is_single")
	@Expose
	var isSingle: Int = 0,
	@SerializedName("materials")
	@Expose
	var materials: List<Material> = listOf(),
	@SerializedName("left_materials")
	@Expose
	var leftMaterials: List<Material> = listOf(),
	@SerializedName("right_materials")
	@Expose
	var rightMaterials: List<Material> = listOf(),
	@SerializedName("is_half")
	@Expose
	var half: Int = 0
) : BaseObservable(), Parcelable {

	@SerializedName("qty")
	@Expose
	var qty: Int = 0
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.qty)
		}

	@SerializedName("left_qty")
	@Expose
	var leftQty: Int = 0
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.leftQty)
		}

	@SerializedName("right_qty")
	@Expose
	var rightQty: Int = 0
		@Bindable get() = field
		set(value) {
			field = value
			notifyPropertyChanged(BR.rightQty)
		}

	fun reset() {
		qty = 0
		leftQty = 0
		rightQty = 0
		for (i in 0 until materials.size) {
			materials[i].reset()
		}
		for (i in 0 until rightMaterials.size) {
			rightMaterials[i].reset()
		}
		for (i in 0 until leftMaterials.size) {
			leftMaterials[i].reset()
		}
	}

	constructor(source: Parcel) : this(
		source.readInt(),
		source.readInt(),
		source.readString() ?: "",
		source.readString() ?: "",
		source.readInt(),
		source.readInt(),
		ArrayList<Int>().apply { source.readList(this as List<*>, Int::class.java.classLoader) },
		source.readInt(),
		source.createTypedArrayList(Material.CREATOR) ?: listOf(),
		source.createTypedArrayList(Material.CREATOR) ?: listOf(),
		source.createTypedArrayList(Material.CREATOR) ?: listOf(),
		source.readInt()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeInt(id)
		writeInt(breadId)
		writeString(name)
		writeString(description)
		writeInt(minChoice)
		writeInt(maxChoice)
		writeList(percent)
		writeInt(isSingle)
		writeTypedList(materials)
		writeTypedList(leftMaterials)
		writeTypedList(rightMaterials)
		writeInt(half)
	}

	override fun toString(): String {
		return "Step(qty=$qty, rightQty=$rightQty, leftQty=$leftQty, materials=$materials, rightMaterials=$rightMaterials, leftMaterials=$leftMaterials)"
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Step> = object : Parcelable.Creator<Step> {
			override fun createFromParcel(source: Parcel): Step = Step(source)
			override fun newArray(size: Int): Array<Step?> = arrayOfNulls(size)
		}
	}
}