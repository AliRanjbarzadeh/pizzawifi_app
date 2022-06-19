package ir.atriatech.pizzawifi.ui.main.home.bloggallery

import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.Blog

class BlogGalleryFullScreenFragmentViewModel : BaseFragmentViewModel() {
    var mListItems = mutableListOf<String>()
    var mItem: Blog = Blog()
    var mAdapter: BlogGalleryFullScreenAdapter? = null
    var currentPosition = 0
}
