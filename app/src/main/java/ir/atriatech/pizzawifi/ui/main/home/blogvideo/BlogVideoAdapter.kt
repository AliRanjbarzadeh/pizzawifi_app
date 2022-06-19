package ir.atriatech.pizzawifi.ui.main.home.blogvideo

import android.net.Uri
import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemBlogVideoBinding
import ir.atriatech.pizzawifi.entities.home.BlogVideo

class BlogVideoAdapter(val recyclerViewTools: RecyclerViewTools) :
    BaseAdapter<BlogVideo>() {

    init {
        component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<BlogVideo> {
        val binding = inflate<ItemBlogVideoBinding>(parent, R.layout.item_blog_video)

        return object : BaseHolder<BlogVideo>(binding) {
            override fun bindUI(item: BlogVideo) {
                binding.item = item

                imageLoader.load(
                    Uri.parse(item.blogVideoInfo.image),
                    binding.imgProduct
                )

                binding.root.setOnClickListener {
                    recyclerViewTools.onItemClick(bindingAdapterPosition, it, item)
                }

                binding.executePendingBindings()
            }
        }
    }
}