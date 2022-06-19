package ir.atriatech.pizzawifi.ui.main.shopcart

import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.gson.Gson
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.Ext
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemShopcartBinding
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import javax.inject.Inject

class ShopCartAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<ShopCartItem>() {

    @Inject
    lateinit var gson: Gson

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ShopCartItem> {
        val binding = inflate<ItemShopcartBinding>(parent, R.layout.item_shopcart)

        return object : BaseHolder<ShopCartItem>(binding) {
            override fun bindUI(item: ShopCartItem) {
                binding.item = item

                //Set text animation
                binding.txtCount.inAnimation =
                    AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_in)
                binding.txtCount.outAnimation =
                    AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_out)

//                val discountPercent = "${item.discount_percent}%"
//                binding.txtDiscount.text = discountPercent

                binding.btnIncrease.setOnClickListener {
                    recyclerViewTools.onIncreaseItem(
                        bindingAdapterPosition,
                        binding.root,
                        item
                    )
                }
                binding.btnDecrease.setOnClickListener {
                    recyclerViewTools.onDecreaseItem(
                        bindingAdapterPosition,
                        binding.root,
                        item
                    )
                }

                when {
                    item.qty == 1 -> binding.btnDecrease.setImageResource(R.drawable.ic_trash2)

                    item.qty > 1 -> binding.btnDecrease.setImageResource(R.drawable.ic_baseline_remove_24)
                }
//				binding.btnDelete.setOnClickListener { recyclerViewTools.onDeleteClick(bindingAdapterPosition, it, item) }

                binding.imgProduct.setImageDrawable(null)
                if (item.image.isNotEmpty() && item.pizza == 0) {
                    imageLoader.loadFitCenter(
                        item.image.getImageUri("", true),
                        binding.imgProduct
                    )
                } else if (item.pizza == 1) {
                    binding.imgProduct.setImageResource(R.drawable.ic_default_maker_pizza)
                } else {
                    binding.imgProduct.setImageResource(R.drawable.product_test)
                }
                binding.executePendingBindings()
            }
        }
    }
}