package ir.atriatech.pizzawifi.ui.main.profile.competition

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemCompetitionBinding
import ir.atriatech.pizzawifi.entities.profile.competition.Competition

class CompetitionAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Competition>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Competition> {
        val binding = inflate<ItemCompetitionBinding>(parent, R.layout.item_competition)

        return object : BaseHolder<Competition>(binding) {
            override fun bindUI(item: Competition) {
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