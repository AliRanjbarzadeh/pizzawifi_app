package ir.atriatech.pizzawifi.ui.main.home

import android.graphics.Color
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemHomeBottomBinding
import ir.atriatech.pizzawifi.databinding.ItemHomeMiddleBinding
import ir.atriatech.pizzawifi.databinding.ItemHomeTopBinding
import ir.atriatech.pizzawifi.databinding.ItemWarningBinding
import ir.atriatech.pizzawifi.entities.home.HomeModel1
import ir.atriatech.pizzawifi.entities.home.HomeModel2
import ir.atriatech.pizzawifi.entities.home.HomeModel3
import ir.atriatech.pizzawifi.ui.main.home.product.HomeProductAdapter

class HomeAdapter(homeShowPrice: Int) : BaseAdapter<Any>() {

	private var viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
	private val mAdapters = SparseArray<HomeProductAdapter>()
	private val mPositions = SparseArray<String>()
	private val mOffsets = SparseArray<String>()
	private val showPrice = homeShowPrice

	override fun getItemViewType(position: Int): Int {
		return when (list[position]) {
			is HomeModel1 -> R.layout.item_home_top

			is HomeModel2 -> R.layout.item_home_middle

			else -> 0
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Any> {
		return when (viewType) {
			R.layout.item_home_top -> {//Top item
				val binding = inflate<ItemHomeTopBinding>(parent, viewType)
				object : BaseHolder<Any>(binding) {
					override fun bindUI(item: Any) {
						item as HomeModel1
						binding.item = item
						binding.restaurantMenu.setOnClickListener { onClick(it, item) }
						binding.makePizza.setOnClickListener { onClick(it, item) }
						binding.selectCity.setOnClickListener { onClick(it, item) }
						binding.executePendingBindings()
					}

					fun onClick(v: View, item: HomeModel1) {
						item.recyclerViewTools.onItemClick(bindingAdapterPosition, v, item)
					}
				}
			}

			R.layout.item_home_middle -> {//Middle item
				val binding = inflate<ItemHomeMiddleBinding>(parent, viewType)
				object : BaseHolder<Any>(binding) {
					override fun bindUI(item: Any) {
						binding.item = item as HomeModel2

						//Set color of dot and line category
						if (item.color.isNotEmpty()) {
							binding.itemDot.setBackgroundColor(Color.parseColor(item.color))
							binding.itemLine.setBackgroundColor(Color.parseColor(item.color))
						}

						setAdapter(item)
					}

					fun setAdapter(item: HomeModel2) {
						if (mAdapters[bindingAdapterPosition] == null) {
							mAdapters.append(
								bindingAdapterPosition,
								HomeProductAdapter(showPrice, item.recyclerViewTools)
							)
							mAdapters[bindingAdapterPosition].parentPosition = bindingAdapterPosition
							mAdapters[bindingAdapterPosition].addToList(item.blogs)
						}
						binding.rvProduct.setRecycledViewPool(viewPool)
						binding.rvProduct.setHasFixedSize(true)
						binding.rvProduct.layoutManager =
							LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
						binding.rvProduct.adapter = mAdapters[bindingAdapterPosition]

						if (mPositions[bindingAdapterPosition] != null && mOffsets[bindingAdapterPosition] != null) {
							(binding.rvProduct.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
								mPositions[bindingAdapterPosition].toInt(),
								(mOffsets[bindingAdapterPosition].toInt() / 3.5).toInt()
							)
						}

						binding.rvProduct.addOnScrollListener(object :
							RecyclerView.OnScrollListener() {
							override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
								val layoutManager =
									recyclerView.layoutManager as LinearLayoutManager
								val firstCompletePosition =
									layoutManager.findFirstCompletelyVisibleItemPosition()
								mPositions.append(bindingAdapterPosition, firstCompletePosition.toString())
								val viewOffset = when (val startView =
									recyclerView.findViewHolderForAdapterPosition(
										firstCompletePosition
									)?.itemView) {
									null -> 0

									else -> {
										startView.right
									}
								}
								mOffsets.append(bindingAdapterPosition, viewOffset.toString())

								super.onScrolled(recyclerView, dx, dy)
							}
						})
					}
				}
			}

			R.layout.item_home_bottom -> {//Bottom item
				val binding = inflate<ItemHomeBottomBinding>(parent, viewType)
				object : BaseHolder<Any>(binding) {
					override fun bindUI(item: Any) {
						item as HomeModel3
						binding.item = item

						binding.btnTournament.setOnClickListener {
							item.recyclerViewTools.onItemClick(
								bindingAdapterPosition,
								it,
								item
							)
						}
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