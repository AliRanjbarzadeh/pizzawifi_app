package ir.atriatech.pizzawifi.entities.customerMenu


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.pizzawifi.entities.ProductMaterial

data class CustomerProduct(
    @SerializedName("comment_count")
    @Expose
    var commentCount: Int = 0,
    @SerializedName("entity")
    @Expose
    var entity: Int = 0,
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("price")
    @Expose
    var price: Int = 0,
    @SerializedName("sender")
    @Expose
    var sender: String = "",
    @SerializedName("materials")
    @Expose
    var materials: MutableList<ProductMaterial> = mutableListOf()
) : BaseObservable(), Parcelable {
    @SerializedName("rate")
    @Expose
    var rate: Float = 0f
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.rate)
        }

    var shopCount: Int = 0
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.shopCount)
        }

    fun getFormatPrice() = price.priceFormat()

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readString() ?: "",
        source.readInt(),
        source.readString() ?: "",
        source.createTypedArrayList(ProductMaterial.CREATOR) ?: mutableListOf()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(commentCount)
        writeInt(entity)
        writeInt(id)
        writeString(name)
        writeInt(price)
        writeString(sender)
        writeTypedList(materials)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CustomerProduct> =
            object : Parcelable.Creator<CustomerProduct> {
                override fun createFromParcel(source: Parcel): CustomerProduct =
                    CustomerProduct(source)

                override fun newArray(size: Int): Array<CustomerProduct?> = arrayOfNulls(size)
            }
    }
}