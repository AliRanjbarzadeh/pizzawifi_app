package ir.atriatech.pizzawifi.ui.main.home.blogvideo

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.Blog
import ir.atriatech.pizzawifi.entities.home.BlogVideo
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class BlogVideoFragmentViewModel : BaseFragmentViewModel() {
    var mListItems = mutableListOf<BlogVideo>()
    var mItem: Blog = Blog()
    var mAdapter: BlogVideoAdapter? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Blog>> by lazy { repository.blogOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (mAdapter == null) {
            repository.blogDetail(mItem.id, bag)
            return false;
        }
        return true
    }
}
