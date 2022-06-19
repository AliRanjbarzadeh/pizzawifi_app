package ir.atriatech.pizzawifi.ui.main.more.about

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.GeneralModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class AboutFragmentViewModel : BaseFragmentViewModel() {
    var mItem: GeneralModel? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<GeneralModel>> by lazy { repository.moreOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (mItem == null) {
            repository.about(bag)
            return false
        }
        return true
    }
}
