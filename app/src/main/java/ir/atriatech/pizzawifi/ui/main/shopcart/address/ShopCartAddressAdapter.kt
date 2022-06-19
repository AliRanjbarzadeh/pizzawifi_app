package ir.atriatech.pizzawifi.ui.main.shopcart.address

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemShopCartAddressBinding
import ir.atriatech.pizzawifi.entities.profile.address.Address

class ShopCartAddressAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Address>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Address> {
		val binding = inflate<ItemShopCartAddressBinding>(parent, R.layout.item_shop_cart_address)

		return object : BaseHolder<Address>(binding) {
			override fun bindUI(item: Address) {
				binding.item = item

				//Item click
				binding.root.setOnClickListener {
					recyclerViewTools.onItemSelect(
						bindingAdapterPosition,
						it,
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