package ir.atriatech.pizzawifi.ui.main.profile.address

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemAddressBinding
import ir.atriatech.pizzawifi.entities.profile.address.Address

class AddressAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Address>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Address> {
		val binding = inflate<ItemAddressBinding>(parent, R.layout.item_address)

		return object : BaseHolder<Address>(binding) {
			override fun bindUI(item: Address) {
				binding.item = item

				//Item click
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						binding.root,
						item
					)
				}

				//Tools click
				binding.imgTools.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						binding.root,
						item
					)
				}

				binding.executePendingBindings()
			}
		}
	}
}