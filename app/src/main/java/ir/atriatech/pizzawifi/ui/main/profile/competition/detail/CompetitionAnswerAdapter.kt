package ir.atriatech.pizzawifi.ui.main.profile.competition.detail

import android.view.View
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemAnswerBinding
import ir.atriatech.pizzawifi.entities.profile.competition.Competition
import ir.atriatech.pizzawifi.entities.profile.competition.CompetitionAnswer
import ir.atriatech.pizzawifi.entities.profile.competition.CompetitionQuestion

class CompetitionAnswerAdapter(
    private val competition: Competition,
    private val competitionQuestion: CompetitionQuestion
) : BaseAdapter<CompetitionAnswer>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<CompetitionAnswer> {
        val binding = inflate<ItemAnswerBinding>(parent, R.layout.item_answer)

        return object : BaseHolder<CompetitionAnswer>(binding) {
            override fun bindUI(item: CompetitionAnswer) {
                binding.item = item
                if (competition.isEnd) {
                    binding.imgStatus.visibility = View.VISIBLE
                    if (competitionQuestion.correctAnswer == item.id) {
                        binding.root.setBackgroundColor(findColor(R.color.color50))
                        binding.imgStatus.setImageResource(R.drawable.ic_check_circle_white)
                        binding.txtTitle.setTextColor(findColor(R.color.colorPrimaryDark))
                    } else if (competitionQuestion.userSelect == item.id) {
                        binding.root.setBackgroundColor(findColor(R.color.colorF24))
                        binding.imgStatus.setImageResource(R.drawable.ic_cancel_circle_white)
                    }
                } else {
                    if (item.isUserAnswer) {
                        binding.root.setBackgroundColor(findColor(R.color.color4A4))
                    }
                }
                binding.executePendingBindings()
            }
        }
    }
}