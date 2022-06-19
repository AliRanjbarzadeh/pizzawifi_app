package ir.atriatech.pizzawifi.ui.main.profile.support

import android.content.Context
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findColorStateList
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemSupportBinding
import ir.atriatech.pizzawifi.entities.profile.support.Support

class SupportAdapter(val context: Context, val recyclerViewTools: RecyclerViewTools) :
	BaseAdapter<Support>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Support> {
		val binding = inflate<ItemSupportBinding>(parent, R.layout.item_support)

		return object : BaseHolder<Support>(binding) {
			override fun bindUI(item: Support) {
				binding.item = item

				when (item.status) {
					0 -> {// pending
						binding.btnStatus.backgroundTintList = findColorStateList(R.color.supportStatus0)
						binding.txtStatus.setTextColor(findColor(R.color.supportStatus0))
					}
					1 -> {// admin response
						binding.btnStatus.backgroundTintList = findColorStateList(R.color.supportStatus1)
						binding.txtStatus.setTextColor(findColor(R.color.supportStatus1))
					}
					2 -> {// user response
						binding.btnStatus.backgroundTintList = findColorStateList(R.color.supportStatus2)
						binding.txtStatus.setTextColor(findColor(R.color.supportStatus2))
					}
					3 -> {// closed
						binding.btnStatus.backgroundTintList = findColorStateList(R.color.supportStatus3)
						binding.txtStatus.setTextColor(findColor(R.color.supportStatus3))
					}
					else -> {// unknown
						binding.btnStatus.backgroundTintList = findColorStateList(R.color.supportStatusUnknown)
						binding.txtStatus.setTextColor(findColor(R.color.supportStatusUnknown))
					}
				}

				//Item click
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