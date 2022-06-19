package ir.atriatech.pizzawifi.ui.main.maker.products

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
import ir.atriatech.pizzawifi.databinding.ItemMakerProductBinding
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.ProductMaterial
import java.lang.reflect.Type
import javax.inject.Inject

class MakerProductAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Product>() {

    @Inject
    lateinit var appDataBase: AppDataBase

    @Inject
    lateinit var gson: Gson

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Product> {
        val binding = inflate<ItemMakerProductBinding>(parent, R.layout.item_maker_product)

        return object : BaseHolder<Product>(binding) {
            override fun bindUI(item: Product) {
                binding.item = item

                if (item.productMaterials.isEmpty()) {
                    try {
                        val listType: Type =
                            object : TypeToken<MutableList<ProductMaterial>>() {}.type
                        item.productMaterials = gson.fromJson(item.serverMaterials, listType)
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
                imageLoader.load(
                    item.image.getImageUri(isCustomSize = true, size = "2x"),
                    binding.imgProduct
                )

                binding.executePendingBindings()
            }
        }
    }
}