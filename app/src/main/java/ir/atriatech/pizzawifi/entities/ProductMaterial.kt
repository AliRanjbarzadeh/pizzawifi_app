package ir.atriatech.pizzawifi.entities


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductMaterial(
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("product_id")
    @Expose
    var productId: Int = 0
) : BaseObservable(), Parcelable {
    @SerializedName("default_check")
    @Expose
    var defaultCheck: Int = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.defaultCheck)
        }

    var fakeCheck: Int = 0

    fun setFakeData() {
        fakeCheck = defaultCheck
    }

    fun reset() {
        defaultCheck = fakeCheck
        fakeCheck = 0
    }

    constructor(source: Parcel) : this(
        id = source.readInt(),
        name = source.readString() ?: "",
        productId = source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeInt(productId)
    }

    override fun toString(): String {
        return "ProductMaterial(id=$id, name='$name', defaultCheck=$defaultCheck, fakeCheck=$fakeCheck)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProductMaterial> =
            object : Parcelable.Creator<ProductMaterial> {
                override fun createFromParcel(source: Parcel): ProductMaterial =
                    ProductMaterial(source)

                override fun newArray(size: Int): Array<ProductMaterial?> = arrayOfNulls(size)
            }
    }
}