package ir.atriatech.pizzawifi.ui.main.home.product

import android.view.ViewGroup
import com.google.gson.Gson
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.databinding.ItemHomeProductBinding
import ir.atriatech.pizzawifi.entities.home.Blog
import javax.inject.Inject

class HomeProductAdapter(homeShowPrice: Int, val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<Blog>() {

    private var showPrice = homeShowPrice

    @Inject
    lateinit var appDataBase: AppDataBase

    @Inject
    lateinit var gson: Gson

    var parentPosition = -1

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Blog> {
        val binding = inflate<ItemHomeProductBinding>(parent, R.layout.item_home_product)

        return object : BaseHolder<Blog>(binding) {
            override fun bindUI(item: Blog) {
                binding.item = item
                //Detail click
                binding.btnShowMore.setOnClickListener {
                    recyclerViewTools.onItemClick(
                        bindingAdapterPosition,
                        binding.root,
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

                //Load image
                imageLoader.load(
                    uri = item.image.getImageUri(isCustomSize = true, size = "4x"),
                    imageView = binding.imgProduct,
                    isCenterCrop = false
                )

                //Load icon
                imageLoader.load(
                    uri = item.icon.getImageUri(isCustomSize = true, size = "3x"),
                    imageView = binding.imgTitle,
                    isCenterCrop = false
                )
                binding.executePendingBindings()
            }
        }
    }
}