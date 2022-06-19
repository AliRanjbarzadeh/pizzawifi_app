package ir.atriatech.pizzawifi.ui.main.orders.detail

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.base.MyItemDecoration
import ir.atriatech.pizzawifi.databinding.ItemOrderdetailAddressPriceBinding
import ir.atriatech.pizzawifi.databinding.ItemOrderdetailProductsBinding
import ir.atriatech.pizzawifi.databinding.ItemOrderdetailTimerBinding
import ir.atriatech.pizzawifi.databinding.ItemWarningBinding
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.OrderItem
import ir.atriatech.pizzawifi.entities.orders.detail.OrderTimer

class OrderDetailAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Any>() {

    private var viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    private val mAdapters = SparseArray<OrderItemsAdapter>()

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is OrderTimer -> R.layout.item_orderdetail_timer

            is MutableList<*> -> R.layout.item_orderdetail_products

            is Order -> R.layout.item_orderdetail_address_price

            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Any> {
        return when (viewType) {
            R.layout.item_orderdetail_timer -> {//Top item
                val binding = inflate<ItemOrderdetailTimerBinding>(parent, viewType)
                lateinit var orderTimer: OrderTimer
                object : BaseHolder<Any>(binding) {
                    override fun bindUI(item: Any) {
                        binding.item = item as OrderTimer
                        orderTimer = item
                        binding.executePendingBindings()
                    }

                    override fun refreshTimer(leftTime: Long) {
                        if (leftTime > 0) {
                            binding.mTimer.start(leftTime)
                        } else {
                            binding.mTimer.stop()
                            binding.mTimer.allShowZero()
                        }
                    }

                    override fun changeTimerStatus(isStop: Boolean) {
                        when (isStop) {
                            true -> {
                                binding.mTimer.stop()
                                orderTimer.waitTime = binding.mTimer.remainTime
                            }

                            false -> {
                                if (binding.mTimer.remainTime > 0) {
                                    refreshTimer(orderTimer.waitTime)
                                } else if (orderTimer.waitTime > 0) {
                                    refreshTimer(orderTimer.waitTime)
                                }
                            }
                        }
                    }
                }
            }

            R.layout.item_orderdetail_products -> {//Middle item
                val binding = inflate<ItemOrderdetailProductsBinding>(parent, viewType)
                object : BaseHolder<Any>(binding) {
                    override fun bindUI(item: Any) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            item as MutableList<OrderItem>
                            setAdapter(item)
                        } catch (e: Exception) {
                        } finally {
                            binding.executePendingBindings()
                        }
                    }

                    fun setAdapter(orderItems: MutableList<OrderItem>) {
                        if (mAdapters[bindingAdapterPosition] == null) {
                            mAdapters.append(bindingAdapterPosition, OrderItemsAdapter())
                            mAdapters[bindingAdapterPosition].addToList(orderItems)
                        }
                        binding.rvOrderItems.setRecycledViewPool(viewPool)
                        binding.rvOrderItems.setHasFixedSize(false)
                        binding.rvOrderItems.isNestedScrollingEnabled = false
                        binding.rvOrderItems.layoutManager = LinearLayoutManager(ctx)
                        binding.rvOrderItems.adapter = mAdapters[bindingAdapterPosition]
                        binding.rvOrderItems.addItemDecoration(
                            MyItemDecoration(
                                findColor(R.color.colorAA53),
                                dp2px(1)
                            )
                        )
                    }
                }
            }

            R.layout.item_orderdetail_address_price -> {//Bottom item
                val binding = inflate<ItemOrderdetailAddressPriceBinding>(parent, viewType)
                object : BaseHolder<Any>(binding) {
                    override fun bindUI(item: Any) {
                        binding.item = item as Order
                        binding.btnReorder.setOnClickListener {
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
                val warningView = inflate<ItemWarningBinding>(parent, R.layout.item_warning)
                object : BaseHolder<Any>(warningView) {
                    override fun bindUI(item: Any) {
                    }
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseHolder<Any>) {
        super.onViewDetachedFromWindow(holder)

        when (holder.itemViewType) {
            R.layout.item_orderdetail_timer -> {
                holder.changeTimerStatus(true)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: BaseHolder<Any>) {
        super.onViewAttachedToWindow(holder)

        when (holder.itemViewType) {
            R.layout.item_orderdetail_timer -> {
                holder.changeTimerStatus(false)
            }
        }
    }
}