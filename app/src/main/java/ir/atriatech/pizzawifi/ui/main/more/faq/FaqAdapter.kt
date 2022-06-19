package ir.atriatech.pizzawifi.ui.main.more.faq

import android.view.View
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemFaqBinding
import ir.atriatech.pizzawifi.entities.more.faq.Faq

class FaqAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Faq>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Faq> {
        val binding = inflate<ItemFaqBinding>(parent, R.layout.item_faq)

        return object : BaseHolder<Faq>(binding) {
            override fun bindUI(item: Faq) {
                binding.item = item
                if (item.isExpanded) {
                    binding.imgMore.setImageResource(R.drawable.ic_chevron_down_f96dp)
                    binding.txtValue.maxLines = Integer.MAX_VALUE
                } else {
                    binding.imgMore.setImageResource(R.drawable.ic_chevron_left_f96dp)
                    binding.txtValue.maxLines = 2
                }
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