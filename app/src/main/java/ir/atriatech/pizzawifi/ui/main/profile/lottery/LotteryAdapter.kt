package ir.atriatech.pizzawifi.ui.main.profile.lottery

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemLotteryBinding
import ir.atriatech.pizzawifi.entities.profile.lottery.LotteryModel

class LotteryAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<LotteryModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<LotteryModel> {
        val binding = inflate<ItemLotteryBinding>(parent, R.layout.item_lottery)

        return object : BaseHolder<LotteryModel>(binding) {
            override fun bindUI(item: LotteryModel) {
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