package ir.atriatech.pizzawifi.ui.main.city

import android.content.Context
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemCityBinding
import ir.atriatech.pizzawifi.entities.CityModel

class CityAdapter(val context: Context, val recyclerViewTools: RecyclerViewTools) :
	BaseAdapter<CityModel>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<CityModel> {
		val binding = inflate<ItemCityBinding>(parent, R.layout.item_city)
		return object : BaseHolder<CityModel>(binding) {
			override fun bindUI(item: CityModel) {
				binding.item = item

				if (item.isSelected) {
					binding.cvMain.setCardBackgroundColor(findColor(R.color.color22))
				} else {
					binding.cvMain.setCardBackgroundColor(findColor(R.color.colorF4))
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