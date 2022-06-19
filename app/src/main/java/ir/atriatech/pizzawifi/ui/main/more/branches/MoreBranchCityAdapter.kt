package ir.atriatech.pizzawifi.ui.main.more.branches

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.Ext
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMoreBranchCityBinding
import ir.atriatech.pizzawifi.entities.CityModel

class MoreBranchCityAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<CityModel>() {

	private var viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
	private val mAdapters = SparseArray<MoreBranchAdapter>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<CityModel> {
		val binding = inflate<ItemMoreBranchCityBinding>(parent, R.layout.item_more_branch_city)

		return object : BaseHolder<CityModel>(binding) {
			override fun bindUI(item: CityModel) {
				binding.item = item
				setAdapter(item)
				binding.executePendingBindings()
			}

			fun setAdapter(cityModel: CityModel) {
				if (mAdapters[bindingAdapterPosition] == null) {
					mAdapters.append(
						bindingAdapterPosition,
						MoreBranchAdapter(recyclerViewTools)
					)
					mAdapters[bindingAdapterPosition].addToList(cityModel.branches)
				}

				binding.rvBranch.setRecycledViewPool(viewPool)
				binding.rvBranch.setHasFixedSize(false)
				binding.rvBranch.isNestedScrollingEnabled = false
				binding.rvBranch.layoutManager = LinearLayoutManager(Ext.ctx)
				binding.rvBranch.adapter = mAdapters[bindingAdapterPosition]
			}
		}
	}
}