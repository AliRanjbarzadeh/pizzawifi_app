package ir.atriatech.pizzawifi.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat

data class Product(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("entity")
    @Expose
    var entity: Int = 1
) : BaseObservable(), Parcelable {
    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("description")
    @Expose
    var description: String = ""

    @SerializedName("price")
    @Expose
    var price: Int = 0

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Int = 0

    @SerializedName("discount_percent")
    @Expose
    var discount_percent: Int = 0

    @SerializedName("image")
    @Expose
    var image: String = ""

    @SerializedName("base_price")
    @Expose
    var basePrice: Int = 0

    @SerializedName("type")
    @Expose
    var type: Int = 0 // 0: product pizza  1: maker  2: other

    @SerializedName("max_choice")
    @Expose
    var max_choice: Int = 0

    @SerializedName("materials")
    @Expose
    var serverMaterials: JsonElement? = null

    val discountPercentString: String
        get() {
            return "$discount_percent%"
        }

    var productMaterials: MutableList<ProductMaterial> = mutableListOf()

    var shopCount: Int = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.shopCount)
        }

    var fakeCount: Int = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.fakeCount)
        }

    var isInDb: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.inDb)
        }

    fun getFormatPrice(): String = (price - discountAmount).priceFormat()
    fun getFormatRealPrice(): String = price.priceFormat()
    fun getTotalPrice(): String = (fakeCount * (price - discountAmount)).priceFormat()

    fun setFakeData() {
        productMaterials.forEach { productMaterial ->
            productMaterial.setFakeData()
        }
    }

    fun reset() {
        fakeCount = 0
    }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeInt(entity)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
            override fun createFromParcel(source: Parcel): Product = Product(source)
            override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "Product(id=$id, name='$name')"
    }
}