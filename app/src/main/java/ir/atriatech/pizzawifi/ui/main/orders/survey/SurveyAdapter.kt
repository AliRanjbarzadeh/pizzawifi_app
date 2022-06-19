package ir.atriatech.pizzawifi.ui.main.orders.survey

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemSurveyDescriptionBinding
import ir.atriatech.pizzawifi.databinding.ItemSurveyRateBinding
import ir.atriatech.pizzawifi.entities.orders.survey.Question

class SurveyAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Question>() {

    override fun getItemViewType(position: Int): Int {
        return when (list[position].id) {
            0 -> R.layout.item_survey_description
            else -> R.layout.item_survey_rate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Question> {
        return when (viewType) {
            R.layout.item_survey_description -> {
                val binding =
                    inflate<ItemSurveyDescriptionBinding>(parent, R.layout.item_survey_description)
                object : BaseHolder<Question>(binding) {
                    override fun bindUI(item: Question) {
                        binding.item = item
                        binding.btnAdd.setOnClickListener {
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

            else -> {
                val binding = inflate<ItemSurveyRateBinding>(parent, R.layout.item_survey_rate)
                object : BaseHolder<Question>(binding) {
                    override fun bindUI(item: Question) {
                        binding.item = item
                        binding.executePendingBindings()
                    }
                }
            }
        }
    }
}