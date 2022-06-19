package ir.atriatech.pizzawifi.ui.main.more.area

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.more.area.AreaItem
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class AreaFragmentViewModel : BaseFragmentViewModel() {
    var mListItems = mutableListOf<AreaItem>()

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<AreaItem>>> by lazy {
        repository.areaOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (mListItems.isEmpty()) {
            repository.area(bag)
            return false
        }
        return true
    }
}
