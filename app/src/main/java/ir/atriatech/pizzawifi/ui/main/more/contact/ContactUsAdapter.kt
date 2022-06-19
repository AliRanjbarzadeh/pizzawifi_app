package ir.atriatech.pizzawifi.ui.main.more.contact

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemContactUsBinding
import ir.atriatech.pizzawifi.databinding.ItemContactUsTitleBinding
import ir.atriatech.pizzawifi.entities.more.contactus.ContactItem

class ContactUsAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<ContactItem>() {

	override fun getItemViewType(position: Int): Int {
		return if (list[position].isHasTitle) {
			R.layout.item_contact_us_title
		} else {
			R.layout.item_contact_us
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ContactItem> {

		if (viewType == R.layout.item_contact_us_title) {
			val binding = inflate<ItemContactUsTitleBinding>(parent, R.layout.item_contact_us_title)
			return object : BaseHolder<ContactItem>(binding) {
				override fun bindUI(item: ContactItem) {
					binding.item = item
					binding.executePendingBindings()
				}
			}
		}

		val binding = inflate<ItemContactUsBinding>(parent, R.layout.item_contact_us)
		return object : BaseHolder<ContactItem>(binding) {
			override fun bindUI(item: ContactItem) {
				binding.item = item
				binding.imgIcon.setImageResource(item.image)

				//On item click
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