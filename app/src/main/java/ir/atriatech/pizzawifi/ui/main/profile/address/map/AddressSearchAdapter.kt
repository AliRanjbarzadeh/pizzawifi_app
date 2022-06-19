package ir.atriatech.pizzawifi.ui.main.profile.address.map

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemAddressSearchBinding
import ir.atriatech.pizzawifi.entities.profile.address.AddressSearch

class AddressSearchAdapter(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<AddressSearch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<AddressSearch> {
        val binding = inflate<ItemAddressSearchBinding>(parent, R.layout.item_address_search)

        return object : BaseHolder<AddressSearch>(binding) {
            override fun bindUI(item: AddressSearch) {
                binding.item = item
                binding.root.setOnClickListener {
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