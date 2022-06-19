package ir.atriatech.pizzawifi.entities.shopcart

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.atriatech.extensions.kotlin.priceFormat

@Entity(tableName = "products")
data class ShopCartItem(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : BaseObservable() {
    var productID: Int = 0
    var name: String = ""
    var image: String = ""
    var price: Int = 0
    var discount: Int = 0
    var discount_percent: Int = 0
    var type: Int = 0
    var max_choice: Int = 0
    var qty: Int = 0
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.qty)
            notifyPropertyChanged(BR.totalPrice)
        }
    var branchId: Int = 0
    var branchName: String = ""
    var branchAddress: String = ""
    var materials: String = ""
    var productFromMaker = 0
    var pizza = 0

    val formatRealPrice: String
        @Bindable get() = price.priceFormat()

    val formatPrice: String
        @Bindable get() = (price - discount).priceFormat()

    val totalPrice
        @Bindable get() = ((price - discount) * qty).priceFormat()

    override fun toString(): String {
        return "ShopCartItem(id=$id, productID=$productID, qty=$qty, name='$name')"
    }
}