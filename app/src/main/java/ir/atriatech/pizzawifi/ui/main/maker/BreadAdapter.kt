package ir.atriatech.pizzawifi.ui.main.maker

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMakerBreadBinding
import ir.atriatech.pizzawifi.entities.home.maker.Bread

class BreadAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Bread>() {
	init {
		component.inject(this)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Bread> {
		val binding = inflate<ItemMakerBreadBinding>(parent, R.layout.item_maker_bread)
		return object : BaseHolder<Bread>(binding) {
			override fun bindUI(item: Bread) {
				binding.item = item
				imageLoader.load(
					item.icon.getImageUri("2x", true),
					binding.imgProduct
				)
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						it,
						item
					)
				}
				binding.imgRemove.setOnClickListener {
					recyclerViewTools.onDeleteClick(
						bindingAdapterPosition,
						binding.root,
						item
					)
				}

				if (item.isSelected) {
					binding.pvMaterial.progress = 1f
				}
				binding.executePendingBindings()
			}
		}
	}
}