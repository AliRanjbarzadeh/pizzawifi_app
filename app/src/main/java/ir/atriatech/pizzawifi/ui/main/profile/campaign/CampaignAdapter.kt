package ir.atriatech.pizzawifi.ui.main.profile.campaign

import android.graphics.Color
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemCampaignBinding
import ir.atriatech.pizzawifi.entities.profile.campaign.Campaign

class CampaignAdapter( val recyclerViewTools: RecyclerViewTools) :
	BaseAdapter<Campaign>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Campaign> {
		val binding = inflate<ItemCampaignBinding>(parent, R.layout.item_campaign)

		return object : BaseHolder<Campaign>(binding) {
			override fun bindUI(item: Campaign) {
				binding.item = item

				//On item click
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						it,
						item
					)
				}
				binding.viewStatus.setCardBackgroundColor(Color.parseColor(item.statusColor))
				binding.executePendingBindings()
			}
		}
	}
}