package ir.atriatech.pizzawifi.ui.main.shopcart

import androidx.recyclerview.widget.DiffUtil
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem

class ShopCartItemDiffUtil(
    private val oldList: MutableList<ShopCartItem>,
    private val newList: MutableList<ShopCartItem>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition]) && oldList[oldItemPosition].qty == newList[newItemPosition].qty && oldList[oldItemPosition].id == newList[newItemPosition].id
    }
}