package ir.atriatech.pizzawifi.ui.main.orders.detail

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemOrderdetailInnerProductBinding
import ir.atriatech.pizzawifi.entities.orders.OrderItem

class OrderItemsAdapter : BaseAdapter<OrderItem>() {

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<OrderItem> {
        val binding = inflate<ItemOrderdetailInnerProductBinding>(
            parent,
            R.layout.item_orderdetail_inner_product
        )

        return object : BaseHolder<OrderItem>(binding) {
            override fun bindUI(item: OrderItem) {
                binding.item = item
                if (item.type == 1) {
                    binding.imgProduct.setImageResource(R.drawable.ic_default_maker_pizza)
                } else {
                    imageLoader.loadFitCenter(
                        item.image.getImageUri("", true),
                        binding.imgProduct
                    )
                }
                binding.executePendingBindings()
            }
        }
    }
}