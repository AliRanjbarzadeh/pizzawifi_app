package ir.atriatech.pizzawifi.ui.main.profile.messages

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMessageBinding
import ir.atriatech.pizzawifi.entities.profile.messages.Message

class MessagesAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Message>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Message> {
        val binding = inflate<ItemMessageBinding>(parent, R.layout.item_message)

        return object : BaseHolder<Message>(binding) {
            override fun bindUI(item: Message) {
                binding.item = item
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