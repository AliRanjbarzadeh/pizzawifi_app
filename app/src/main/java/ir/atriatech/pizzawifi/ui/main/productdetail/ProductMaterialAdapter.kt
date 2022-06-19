package ir.atriatech.pizzawifi.ui.main.productdetail

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemProductMaterialSelectableBinding
import ir.atriatech.pizzawifi.entities.ProductMaterial

class ProductMaterialAdapter(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<ProductMaterial>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ProductMaterial> {
        val binding = inflate<ItemProductMaterialSelectableBinding>(
            parent,
            R.layout.item_product_material_selectable
        )

        return object : BaseHolder<ProductMaterial>(binding) {
            override fun bindUI(item: ProductMaterial) {
                binding.item = item

                binding.materialSwitch.setOnCheckedChangeListener { _, isChecked ->
                    recyclerViewTools.onItemClick(bindingAdapterPosition, binding.root, isChecked)
                }
            }
        }
    }
}