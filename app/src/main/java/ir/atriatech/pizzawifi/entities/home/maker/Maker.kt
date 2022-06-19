package ir.atriatech.pizzawifi.entities.home.maker


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.pizzawifi.entities.Product

data class Maker(
    @SerializedName("desserts")
    @Expose
    var desserts: List<Product> = listOf(),
    @SerializedName("drinks")
    @Expose
    var drinks: List<Product> = listOf(),
    @SerializedName("breads")
    @Expose
    var breads: List<Bread> = listOf()
) : BaseObservable(), Parcelable {
    @SerializedName("title")
    @Expose
    var title: String = ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    @SerializedName("description")
    @Expose
    var description: String = ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
        }

    @SerializedName("products_title")
    @Expose
    var productsTitle: String = ""

    @SerializedName("products_help")
    @Expose
    var productsHelp: String = ""

    @SerializedName("bread_title")
    @Expose
    var breadTitle: String = ""

    @SerializedName("bread_help")
    @Expose
    var breadHelp: String = ""

    @SerializedName("base_price")
    @Expose
    var basePrice: Int = 0

    fun reset() {
        for (i in 0 until breads.size) {
            breads[i].reset()
        }
    }

    fun isHasProduct(): Boolean = desserts.isNotEmpty() || drinks.isNotEmpty()

    fun destroyProducts() {
        if (desserts.isNotEmpty()) {
            for (dessert in desserts) {
                dessert.fakeCount = 0
                dessert.isInDb = false
            }
        }

        if (drinks.isNotEmpty()) {
            for (drink in drinks) {
                drink.fakeCount = 0
                drink.isInDb = false
            }
        }
    }

    constructor(source: Parcel) : this(
        ArrayList<Product>().apply {
            source.readList(
                this as List<*>,
                Product::class.java.classLoader
            )
        },
        ArrayList<Product>().apply {
            source.readList(
                this as List<*>,
                Product::class.java.classLoader
            )
        },
        ArrayList<Bread>().apply { source.readList(this as List<*>, Bread::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(desserts)
        writeList(drinks)
        writeList(breads)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Maker> = object : Parcelable.Creator<Maker> {
            override fun createFromParcel(source: Parcel): Maker = Maker(source)
            override fun newArray(size: Int): Array<Maker?> = arrayOfNulls(size)
        }
    }
}