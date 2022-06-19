package ir.atriatech.pizzawifi.ui.main.more.faq

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.more.faq.Faq
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class FaqFragmentViewModel : BaseFragmentViewModel() {
    var mAdapter: FaqAdapter? = null
    var mListItems = mutableListOf<Faq>()
    var isRefreshing = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Faq>>> by lazy {
        repository.faqOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        when {
            mAdapter == null -> {
                repository.faq(bag)
                return false
            }

            mListItems.isEmpty() -> {
                repository.faq(bag)
                return true
            }
        }
        return true
    }
}
