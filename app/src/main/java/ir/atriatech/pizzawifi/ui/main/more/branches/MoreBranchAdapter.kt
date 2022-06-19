package ir.atriatech.pizzawifi.ui.main.more.branches

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMoreBranchBinding
import ir.atriatech.pizzawifi.entities.Branch

class MoreBranchAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Branch>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Branch> {
		val binding = inflate<ItemMoreBranchBinding>(parent, R.layout.item_more_branch)

		return object : BaseHolder<Branch>(binding) {
			override fun bindUI(item: Branch) {
				binding.item = item

				//Call
				binding.imgCall.setOnClickListener { recyclerViewTools.onItemClick(bindingAdapterPosition, it, item) }

				//Map
				binding.imgLocation.setOnClickListener { recyclerViewTools.onItemSelect(bindingAdapterPosition, it, item) }

				binding.executePendingBindings()
			}
		}
	}
}