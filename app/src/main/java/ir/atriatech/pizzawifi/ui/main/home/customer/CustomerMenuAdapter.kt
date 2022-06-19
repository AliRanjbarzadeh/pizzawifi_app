package ir.atriatech.pizzawifi.ui.main.home.customer

import android.view.ViewGroup
import android.view.animation.AnimationUtils
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.databinding.ItemCustomerProductBinding
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import javax.inject.Inject

class CustomerMenuAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Pizza>() {

    @Inject
    lateinit var appDataBase: AppDataBase

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Pizza> {
        val binding = inflate<ItemCustomerProductBinding>(parent, R.layout.item_customer_product)

        return object : BaseHolder<Pizza>(binding) {
            override fun bindUI(item: Pizza) {
                binding.item = item

                //Item click
                binding.root.setOnClickListener {
                    recyclerViewTools.onItemClick(
                        bindingAdapterPosition,
                        it,
                        item
                    )
                }

                //Set text animation
                binding.txtCount.inAnimation = AnimationUtils.loadAnimation(ctx, R.anim.bounce_in)
                binding.txtCount.outAnimation = AnimationUtils.loadAnimation(ctx, R.anim.bounce_out)

                //Increase & Decrease
                binding.btnIncrease.setOnClickListener {
                    recyclerViewTools.onIncreaseItem(
                        bindingAdapterPosition,
                        it,
                        item
                    )
                }
                binding.btnDecrease.setOnClickListener {
                    recyclerViewTools.onDecreaseItem(
                        bindingAdapterPosition,
                        it,
                        item
                    )
                }

                binding.imgProduct.setImageURI(null)
                if (item.image.isNotEmpty()) {
                    imageLoader.load(
                        item.image.getImageUri(isCustomSize = true, size = "2x"),
                        binding.imgProduct
                    )
                }

                appDataBase.shopDao().findPizzaById(item.id)
                    .observeForever {
                        if (it != null) {
                            if (item.shopCount != it.qty)
                                item.shopCount = it.qty
                        } else {
                            item.shopCount = 0
                        }
                    }
                binding.executePendingBindings()
            }
        }
    }
}