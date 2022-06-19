package ir.atriatech.pizzawifi.ui.main.maker.laststep

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMakerLastProductBinding
import ir.atriatech.pizzawifi.entities.Product

class MakerLastStepProductlAdapter : BaseAdapter<Product>() {
    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Product> {
        val binding = inflate<ItemMakerLastProductBinding>(parent, R.layout.item_maker_last_product)

        return object : BaseHolder<Product>(binding) {
            override fun bindUI(item: Product) {
                binding.item = item

                imageLoader.load(
                    item.image.getImageUri("2x", true),
                    binding.imgProduct
                )
                binding.executePendingBindings()
            }
        }
    }
}