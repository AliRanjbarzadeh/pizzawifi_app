package ir.atriatech.pizzawifi.ui.main.profile.pizza

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemPizzaBinding
import ir.atriatech.pizzawifi.entities.pizza.Pizza

class PizzaAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Pizza>() {

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Pizza> {
        val binding = inflate<ItemPizzaBinding>(parent, R.layout.item_pizza)

        return object : BaseHolder<Pizza>(binding) {
            override fun bindUI(item: Pizza) {
                binding.item = item

                //Item click
                binding.root.setOnClickListener {
                    recyclerViewTools.onItemClick(
                        bindingAdapterPosition,
                        binding.root,
                        item
                    )
                }

                binding.imgProduct.setImageURI(null)
                if (item.image.isNotEmpty()) {
                    imageLoader.load(
                        item.image.getImageUri(isCustomSize = true, size = "2x"),
                        binding.imgProduct
                    )
                }
                binding.executePendingBindings()
            }
        }
    }
}