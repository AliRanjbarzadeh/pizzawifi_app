package ir.atriatech.pizzawifi.ui.main.shopcart.offer

import android.graphics.Paint
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.databinding.ItemProductOfferBinding
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.ProductMaterial
import java.lang.reflect.Type
import javax.inject.Inject

class ProductOfferAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Product>() {

    @Inject
    lateinit var appDataBase: AppDataBase

    @Inject
    lateinit var gson: Gson

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Product> {
        val binding = inflate<ItemProductOfferBinding>(parent, R.layout.item_product_offer)

        return object : BaseHolder<Product>(binding) {
            override fun bindUI(item: Product) {
                binding.item = item

                val discountPercent = "${item.discount_percent}%"
                binding.txtDiscount.text = discountPercent


                if (item.productMaterials.isEmpty()) {
                    try {
                        if (item.type == 3) {
                            val listType: Type =
                                object : TypeToken<MutableList<ProductMaterial>>() {}.type
                            item.productMaterials = gson.fromJson(item.serverMaterials, listType)
                        }
                    } catch (ex: Exception) {
                    }
                }

                //Set text animation
                binding.txtCount.inAnimation = AnimationUtils.loadAnimation(ctx, R.anim.bounce_in)
                binding.txtCount.outAnimation = AnimationUtils.loadAnimation(ctx, R.anim.bounce_out)

                //Add to cart click
                binding.btnAdd.setOnClickListener {
                    recyclerViewTools.onAddToCart(
                        bindingAdapterPosition,
                        it,
                        item
                    )
                }
                binding.root.setOnClickListener {
                    recyclerViewTools.onItemClick(
                        bindingAdapterPosition,
                        it,
                        item
                    )
                }
                /*  if (item.entity == 0) {
                      binding.btnAdd.backgroundTintList = findColorStateList(R.color.colorPrimary)
                  } else {
                      binding.btnAdd.backgroundTintList = findColorStateList(R.color.colorAccent)
                  }

                  //Detail click
                  binding.btnPersonalize.setOnClickListener { recyclerViewTools.onItemClick(bindingAdapterPosition, binding.root, item) }
                  */

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

                //Real price through strike
                binding.txtRealPrice.paintFlags =
                    binding.txtRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                //Load image
                if (item.type == 3) {
                    imageLoader.loadFitCenter(
                        item.image.getImageUri(isCustomSize = true, size = "2x"),
                        binding.imgProduct
                    )
                }

                if (item.type == 3) {
                    appDataBase.shopDao()
                        .findByIdAndMaterial(item.id, item.type, gson.toJson(item.productMaterials))
                        .observeForever {
                            if (it != null) {
                                item.isInDb = true
                                if (item.shopCount != it.qty) {
                                    item.shopCount = it.qty
                                }
                            } else {
                                item.isInDb = false
                                item.shopCount = 0
                            }
                        }
                    if (item.entity == 0) {
                        appDataBase.shopDao().deleteByProductID(item.id)
                    }
                }
                binding.executePendingBindings()
            }
        }
    }
}