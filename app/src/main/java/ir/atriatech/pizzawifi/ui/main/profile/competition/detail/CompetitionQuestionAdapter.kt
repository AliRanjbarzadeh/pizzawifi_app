package ir.atriatech.pizzawifi.ui.main.profile.competition.detail

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemQuestionBinding
import ir.atriatech.pizzawifi.entities.profile.competition.Competition
import ir.atriatech.pizzawifi.entities.profile.competition.CompetitionQuestion

class CompetitionQuestionAdapter(private val competition: Competition) :
    BaseAdapter<CompetitionQuestion>() {
    private val mAdapters = SparseArray<CompetitionAnswerAdapter>()
    private var viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<CompetitionQuestion> {
        val binding = inflate<ItemQuestionBinding>(parent, R.layout.item_question)

        return object : BaseHolder<CompetitionQuestion>(binding) {
            override fun bindUI(item: CompetitionQuestion) {
                item.index = bindingAdapterPosition + 1
                binding.item = item
                setAdapter(item)
                binding.executePendingBindings()
            }

            private fun setAdapter(item: CompetitionQuestion) {
                if (mAdapters[bindingAdapterPosition] == null) {
                    mAdapters.append(bindingAdapterPosition, CompetitionAnswerAdapter(competition, item))
                    mAdapters[bindingAdapterPosition].addToList(item.competitionAnswers)
                }
                binding.rvAnswers.setRecycledViewPool(viewPool)
                binding.rvAnswers.setHasFixedSize(false)
                binding.rvAnswers.isNestedScrollingEnabled = false
                binding.rvAnswers.layoutManager = LinearLayoutManager(ctx)
                binding.rvAnswers.adapter = mAdapters[bindingAdapterPosition]
                binding.rvAnswers.setItemViewCacheSize(item.competitionAnswers.size)
            }
        }
    }
}