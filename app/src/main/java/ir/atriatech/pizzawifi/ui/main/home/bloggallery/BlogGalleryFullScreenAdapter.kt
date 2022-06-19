package ir.atriatech.pizzawifi.ui.main.home.bloggallery

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemBlogGalleryFullScreenBinding

class BlogGalleryFullScreenAdapter(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<String>() {

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<String> {
        val binding = inflate<ItemBlogGalleryFullScreenBinding>(
            parent,
            R.layout.item_blog_gallery_full_screen
        )

        return object : BaseHolder<String>(binding) {
            override fun bindUI(item: String) {
                imageLoader.load(
                    item.getImageUri("4x"),
                    binding.imgProduct,
                    isCenterCrop = false
                )

                binding.executePendingBindings()
            }
        }
    }
}