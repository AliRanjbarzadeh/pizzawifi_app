package ir.atriatech.pizzawifi.ui.main.profile.pizza.detail

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMaterialPizzaDetailBinding
import ir.atriatech.pizzawifi.entities.pizza.PizzaMaterial

class PizzaDetailMaterialAdapter(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<PizzaMaterial>() {

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<PizzaMaterial> {
        val binding =
            inflate<ItemMaterialPizzaDetailBinding>(parent, R.layout.item_material_pizza_detail)

        return object : BaseHolder<PizzaMaterial>(binding) {
            override fun bindUI(item: PizzaMaterial) {
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