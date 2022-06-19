package ir.atriatech.pizzawifi.ui.main.category.product

import android.graphics.Paint
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.databinding.ItemMenuProductBinding
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.ProductMaterial
import java.lang.reflect.Type
import javax.inject.Inject

class ProductAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Product>() {

	@Inject
	lateinit var appDataBase: AppDataBase

	@Inject
	lateinit var gson: Gson

	init {
		component.inject(this)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Product> {
		val binding = inflate<ItemMenuProductBinding>(parent, R.layout.item_menu_product)

		return object : BaseHolder<Product>(binding) {
			override fun bindUI(item: Product) {
				binding.item = item
				val discountPercent = "${item.discount_percent}%"
				binding.txtDiscount.text = discountPercent

				if (item.productMaterials.isEmpty()) {
					try {
						if (item.type == 0) {
							val listType: Type =
								object : TypeToken<MutableList<ProductMaterial>>() {}.type
							item.productMaterials = gson.fromJson(item.serverMaterials, listType)
						}
					} catch (ex: Exception) {
					}
				}

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
				if (item.type == 0) {
					imageLoader.loadFitCenter(
						item.image.getImageUri(isCustomSize = true, size = "2x"),
						binding.imgProduct
					)
				} else {
					//binding.btnPersonalize.text = findString(R.string.pizzaDetail)
					binding.imgProduct.setImageResource(R.drawable.ic_default_maker_pizza)
				}

				if (item.type == 0) {
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

                            when {
                                item.shopCount == 1 -> {
                                    binding.btnDecrease.setIconResource(R.drawable.ic_trash2)
                                }

                                item.shopCount > 1 -> {
                                    binding.btnDecrease.setIconResource(R.drawable.ic_baseline_remove_24)
                                }
                            }
                        }
                    if (item.entity == 0) {
                        appDataBase.shopDao().deleteByProductID(item.id)
                    }
				} else {
                    appDataBase.shopDao().findPizzaById(item.id)
                        .observeForever {
                            if (it != null) {
                                item.isInDb = true
                                if (item.shopCount != it.qty) {
                                    item.shopCount = it.qty
                                }

                                if (item.entity == 0) {
                                    appDataBase.shopDao().deleteByProductID(item.id)
                                }
                            } else {
                                item.isInDb = false
                                item.shopCount = 0
                            }

                            when {
                                item.shopCount == 1 -> {
                                    binding.btnDecrease.setIconResource(R.drawable.ic_trash2)
                                }

                                item.shopCount > 1 -> {
                                    binding.btnDecrease.setIconResource(R.drawable.ic_baseline_remove_24)
                                }
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