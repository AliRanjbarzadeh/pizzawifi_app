package ir.atriatech.pizzawifi.ui.main.maker.stepmaterials

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemMakerMaterialBinding
import ir.atriatech.pizzawifi.entities.home.maker.Material

class MakerStepMaterialsAdapter(val recyclerViewTools: RecyclerViewTools, val isHalf: Boolean = false) :
	BaseAdapter<Material>() {
	init {
		component.inject(this)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Material> {
//        if (isHalf) {
//            val binding = inflate<ItemMakerMaterialBinding>(parent, R.layout.item_maker_material)
//            return object : BaseHolder<Material>(binding) {
//                override fun bindUI(item: Material) {
//                    binding.item = item
//                    imageLoader.load(
//                        item.icon.getImageUri("", true),
//                        binding.imgProduct, false
//                    )
//                    binding.root.setOnClickListener {
//                        recyclerViewTools.onItemClick(
//                            bindingAdapterPosition,
//                            it,
//                            item
//                        )
//                    }
//                    binding.imgRemove.setOnClickListener {
//                        recyclerViewTools.onDeleteClick(
//                            bindingAdapterPosition,
//                            binding.root,
//                            item
//                        )
//                    }
//
//                    binding.executePendingBindings()
//                    if (item.qty > 0) {
//                        when (item.type) {
//                            1 -> {
//                                binding.pvMaterial.progress = 1f
//                            }
//
//                            2 -> {
//                                binding.pvMaterial.progress = item.qty.toFloat() / 2
//                            }
//
//                            3 -> {
//                                if (item.qty == 3) {
//                                    binding.pvMaterial.progress = 1f
//                                } else {
//                                    binding.pvMaterial.progress = item.qty.toFloat() / 3
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
		val binding = inflate<ItemMakerMaterialBinding>(parent, R.layout.item_maker_material)
		return object : BaseHolder<Material>(binding) {
			override fun bindUI(item: Material) {
				binding.item = item
				imageLoader.load(
					item.icon.getImageUri("2x", true),
					binding.imgProduct, false
				)
				binding.root.setOnClickListener {
					recyclerViewTools.onItemClick(
						bindingAdapterPosition,
						it,
						item
					)
				}
				binding.imgRemove.setOnClickListener {
					recyclerViewTools.onDeleteClick(
						bindingAdapterPosition,
						binding.root,
						item
					)
				}

				binding.executePendingBindings()
				if (item.qty > 0) {
					when (item.type) {
						1 -> {
							binding.pvMaterial.progress = 1f
						}

						2 -> {
							binding.pvMaterial.progress = item.qty.toFloat() / 2
						}

						3 -> {
							if (item.qty == 3) {
								binding.pvMaterial.progress = 1f
							} else {
								binding.pvMaterial.progress = item.qty.toFloat() / 3
							}
						}
					}
				}
			}
		}
	}
}