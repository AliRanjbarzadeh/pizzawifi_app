package ir.atriatech.pizzawifi.entities.pizza


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.pizzawifi.entities.home.maker.Bread

data class Pizza(
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
    var price: Int = 0
) : BaseObservable(), Parcelable {
    var shopCount: Int = 0
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.shopCount)
        }

    @SerializedName("materials")
    @Expose
    var serverMaterials: JsonElement? = null

    @SerializedName("is_in_menu")
    @Expose
    var inMenu: Int = 0
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.inMenu)
        }

    @SerializedName("sender")
    @Expose
    var sender: String = ""

    @SerializedName("comment_count")
    @Expose
    var commentCount: Int = 0

    @SerializedName("rate")
    @Expose
    var rate: Float = 0f

    @SerializedName("image")
    @Expose
    var image: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.image)
        }

    var pizzaMaterials: Bread = Bread()

    fun getFormatPrice() = price.priceFormat()

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readString() ?: "",
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(entity)
        writeInt(id)
        writeString(name)
        writeInt(price)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Pizza> = object : Parcelable.Creator<Pizza> {
            override fun createFromParcel(source: Parcel): Pizza = Pizza(source)
            override fun newArray(size: Int): Array<Pizza?> = arrayOfNulls(size)
        }
    }
}