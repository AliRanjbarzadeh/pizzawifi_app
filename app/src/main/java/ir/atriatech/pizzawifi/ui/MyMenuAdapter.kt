package ir.atriatech.pizzawifi.ui

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMyMenuBinding
import ir.atriatech.pizzawifi.databinding.ItemMyMenuMoreBinding
import ir.atriatech.pizzawifi.entities.MyMenuItem

class MyMenuAdapter(val recyclerViewTools: RecyclerViewTools, val isMore: Boolean = false) : BaseAdapter<MyMenuItem>() {

	override fun getItemViewType(position: Int): Int {
		return if (isMore) {
			R.layout.item_my_menu_more
		} else {
			R.layout.item_my_menu
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<MyMenuItem> {
		val binding = if (viewType == R.layout.item_my_menu_more) {
			inflate<ItemMyMenuMoreBinding>(parent, R.layout.item_my_menu_more)
		} else {
			inflate<ItemMyMenuBinding>(parent, R.layout.item_my_menu)
		}

		return object : BaseHolder<MyMenuItem>(binding) {
			override fun bindUI(item: MyMenuItem) {
				if (!isMore) {
					binding as ItemMyMenuBinding
					binding.item = item
					binding.imgIcon.setImageResource(item.image)
				} else {
					binding as ItemMyMenuMoreBinding
					binding.item = item
				}

				//On item click
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						it,
						item
					)
				}

				binding.executePendingBindings()
			}
		}
	}
}