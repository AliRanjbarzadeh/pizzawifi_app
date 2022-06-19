package ir.atriatech.pizzawifi.ui.main.more.contact

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemContactUs2Binding
import ir.atriatech.pizzawifi.entities.more.contactus.ContactItem

class ContactUsAdapterHorizontal(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<ContactItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ContactItem> {
        val binding = inflate<ItemContactUs2Binding>(parent, R.layout.item_contact_us2)

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