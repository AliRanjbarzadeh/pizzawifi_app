package ir.atriatech.pizzawifi.ui.main.home.tournament.questions

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemAnswerCompetitionBinding
import ir.atriatech.pizzawifi.entities.home.tournament.Answer

class TournamentAnswerAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Answer>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Answer> {
        val binding =
            inflate<ItemAnswerCompetitionBinding>(parent, R.layout.item_answer_competition)

        return object : BaseHolder<Answer>(binding) {
            override fun bindUI(item: Answer) {
                binding.item = item
                //Item click
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