package ir.atriatech.pizzawifi.ui.main.branch

import android.content.Context
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemBranchBinding
import ir.atriatech.pizzawifi.entities.Branch
import java.text.NumberFormat
import java.util.*

class BranchAdapter(val context: Context, val recyclerViewTools: RecyclerViewTools) :
	BaseAdapter<Branch>() {
	var numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.US)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Branch> {
		val binding = inflate<ItemBranchBinding>(parent, R.layout.item_branch)
		return object : BaseHolder<Branch>(binding) {
			override fun bindUI(item: Branch) {
				binding.item = item
//                val strDistance = "${numberFormat.format(Integer.valueOf(item.distance))} متر"
//                binding.textView5.text = strDistance

//				if (item.isSelected) {
//					binding.cvMain.setCardBackgroundColor(findColor(R.color.color22))
//				} else {
//					binding.cvMain.setCardBackgroundColor(findColor(R.color.colorF4))
//				}

				binding.imgCall.setOnClickListener { recyclerViewTools.onItemClick(bindingAdapterPosition, it, item) }
				binding.imgLocation.setOnClickListener { recyclerViewTools.onItemClick(bindingAdapterPosition, it, item) }

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