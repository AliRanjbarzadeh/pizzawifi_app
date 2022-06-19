package ir.atriatech.pizzawifi.ui.main.profile.club

import android.net.Uri
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.ItemClubMoreBinding
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemModel

class ClubAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<ClubItemModel>() {

	init {
		component.inject(this)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ClubItemModel> {
		val binding = inflate<ItemClubMoreBinding>(parent, R.layout.item_club_more)

		return object : BaseHolder<ClubItemModel>(binding) {
			override fun bindUI(item: ClubItemModel) {
				binding.item = item

				if (item.rate > 0) {
					binding.pvRate.progress = AppObject.userInfo.club / item.rate.toFloat()
				} else {
					binding.pvRate.progress = 1f
				}

				//Show item
				binding.btnShowItem.setOnClickListener {
					recyclerViewTools.onItemClick(bindingAdapterPosition, it, item)
				}

				//Get item
				binding.btnGetItem.setOnClickListener {
					recyclerViewTools.onItemClick(bindingAdapterPosition, it, item)
				}

				if (item.image.isNotEmpty()) {
					imageLoader.loadFitCenter(
						Uri.parse(item.image),
						binding.imgClubItem
					)
				} else {
					imageLoader.loadFitCenter(
						R.drawable.club_item_default_image,
						binding.imgClubItem
					)
				}

				binding.executePendingBindings()
			}
		}
	}
}