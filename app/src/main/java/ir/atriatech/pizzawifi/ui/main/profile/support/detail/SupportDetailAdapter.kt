package ir.atriatech.pizzawifi.ui.main.profile.support.detail

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemSupportDetail2Binding
import ir.atriatech.pizzawifi.databinding.ItemSupportDetailBinding
import ir.atriatech.pizzawifi.databinding.ItemWarningBinding
import ir.atriatech.pizzawifi.entities.profile.support.Support

class SupportDetailAdapter : BaseAdapter<Support>() {

    override fun getItemViewType(position: Int): Int {
        return when (list[position].isAdmin) {
            true -> R.layout.item_support_detail2

            false -> R.layout.item_support_detail
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Support> {

        return when (viewType) {
            R.layout.item_support_detail -> {//User item
                val binding = inflate<ItemSupportDetailBinding>(parent, viewType)
                object : BaseHolder<Support>(binding) {
                    override fun bindUI(item: Support) {
                        binding.item = item
                        binding.executePendingBindings()
                    }
                }
            }

            R.layout.item_support_detail2 -> {//Admin item
                val binding = inflate<ItemSupportDetail2Binding>(parent, viewType)
                object : BaseHolder<Support>(binding) {
                    override fun bindUI(item: Support) {
                        binding.item = item
                        binding.executePendingBindings()
                    }
                }
            }

            else -> {
                val warningView = inflate<ItemWarningBinding>(parent, R.layout.item_warning)
                object : BaseHolder<Support>(warningView) {
                    override fun bindUI(item: Support) {
                    }
                }
            }
        }
    }
}