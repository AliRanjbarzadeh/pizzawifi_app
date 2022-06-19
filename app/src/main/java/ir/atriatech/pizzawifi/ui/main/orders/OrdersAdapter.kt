package ir.atriatech.pizzawifi.ui.main.orders

import android.content.Context
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemOrderBinding
import ir.atriatech.pizzawifi.entities.orders.Order

class OrdersAdapter(val context: Context, val recyclerViewTools: RecyclerViewTools) :
	BaseAdapter<Order>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Order> {
		val binding = inflate<ItemOrderBinding>(parent, R.layout.item_order)

		return object : BaseHolder<Order>(binding) {
			override fun bindUI(item: Order) {
				binding.item = item

				//On item click
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						it,
						item
					)
				}

				binding.btnOrderDetail.setOnClickListener { recyclerViewTools.onItemClick(bindingAdapterPosition, it, item) }

				when (item.orderStatus) {
					0 -> {// pending
						binding.btnStatus.setTextColor(findColor(R.color.orderStatus0))
					}

					1 -> {// approved
						binding.btnStatus.setTextColor(findColor(R.color.orderStatus1))
					}

					2 -> {// sent
						binding.btnStatus.setTextColor(findColor(R.color.orderStatus2))
					}

					3 -> {// deliver
						binding.btnStatus.setTextColor(findColor(R.color.orderStatus3))
					}

					4 -> {// declined
						binding.btnStatus.setTextColor(findColor(R.color.orderStatus4))
					}
					else -> {// declined
						binding.btnStatus.setTextColor(findColor(R.color.orderStatusUnknown))
					}

				}

				binding.executePendingBindings()
			}
		}
	}
}