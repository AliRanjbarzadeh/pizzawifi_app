package ir.atriatech.pizzawifi.ui

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMaterialMakerDetailBinding
import ir.atriatech.pizzawifi.entities.ProductMaterial

class MakerDetailMaterialAdapter(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<ProductMaterial>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ProductMaterial> {
        val binding =
            inflate<ItemMaterialMakerDetailBinding>(parent, R.layout.item_material_maker_detail)

        return object : BaseHolder<ProductMaterial>(binding) {
            override fun bindUI(item: ProductMaterial) {
                binding.item = item
            }
        }
    }
}