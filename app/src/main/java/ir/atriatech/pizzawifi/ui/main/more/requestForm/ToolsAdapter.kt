package ir.atriatech.pizzawifi.ui.main.more.requestForm

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemReqCityRadioBinding
import ir.atriatech.pizzawifi.databinding.ItemReqProvinceRadioBinding
import ir.atriatech.pizzawifi.databinding.ItemWarningBinding
import ir.atriatech.pizzawifi.databinding.TempRequestSituationBinding
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.ProvinceModel

class ToolsAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Any>() {

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is ProvinceModel -> R.layout.item_req_province_radio

            is CityModel -> R.layout.item_req_city_radio

            is SituationModel -> R.layout.temp_request_situation

            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Any> {
        return when (viewType) {
            R.layout.item_req_province_radio -> {//Top item
                val binding = inflate<ItemReqProvinceRadioBinding>(parent, viewType)

                object : BaseHolder<Any>(binding) {
                    override fun bindUI(item: Any) {
                        binding.item = item as ProvinceModel

                        binding.root.setOnClickListener {
                            recyclerViewTools.onItemClick(
                                bindingAdapterPosition,
                                binding.root, item
                            )
                        }

                        binding.executePendingBindings()
                    }
                }
            }

            R.layout.item_req_city_radio -> {//Middle item
                val binding = inflate<ItemReqCityRadioBinding>(parent, viewType)
                object : BaseHolder<Any>(binding) {
                    override fun bindUI(item: Any) {

                        binding.item = item as CityModel

                        binding.root.setOnClickListener {
                            recyclerViewTools.onItemClick(
                                bindingAdapterPosition,
                                binding.root, item
                            )
                        }

                        binding.executePendingBindings()
                    }
                }
            }

            R.layout.temp_request_situation -> {//Middle item
                val binding = inflate<TempRequestSituationBinding>(parent, viewType)
                object : BaseHolder<Any>(binding) {
                    override fun bindUI(item: Any) {

                        binding.item = item as SituationModel

                        binding.root.setOnClickListener {
                            recyclerViewTools.onItemClick(
                                bindingAdapterPosition,
                                binding.root, item
                            )
                        }

                        binding.executePendingBindings()
                    }
                }
            }

            else -> {
                val warningView = inflate<ItemWarningBinding>(parent, R.layout.item_warning)
                object : BaseHolder<Any>(warningView) {
                    override fun bindUI(item: Any) {
                    }
                }
            }
        }
    }
}