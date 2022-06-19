package ir.atriatech.pizzawifi.ui.main.profile.club

import android.net.Uri
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemClubProfileBinding
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemProfileModel

class UserClubAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<ClubItemProfileModel>() {

	init {
		component.inject(this)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ClubItemProfileModel> {
		val binding = inflate<ItemClubProfileBinding>(parent, R.layout.item_club_profile)

		return object : BaseHolder<ClubItemProfileModel>(binding) {
			override fun bindUI(item: ClubItemProfileModel) {
				binding.item = item

				//Show item
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(bindingAdapterPosition, binding.btnShowItem, item)
				}
				binding.btnShowItem.setOnClickListener {
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