package ir.atriatech.pizzawifi.ui.main.home.customer.detail

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemCommentBinding
import ir.atriatech.pizzawifi.entities.customerMenu.Comment

class CommentAdapter : BaseAdapter<Comment>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Comment> {
        val binding = inflate<ItemCommentBinding>(parent, R.layout.item_comment)

        return object : BaseHolder<Comment>(binding) {
            override fun bindUI(item: Comment) {
                binding.item = item
                binding.executePendingBindings()
            }
        }
    }
}