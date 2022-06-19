package ir.atriatech.pizzawifi.ui.main.more.law

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.GeneralModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class LawFragmentViewModel : BaseFragmentViewModel() {
    var mItem: GeneralModel? = null

    @Inject
    lateinit var repository: MainRepository

    val mObserver: LiveData<Outcome<GeneralModel>> by lazy { repository.moreOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun getData() {
        if (mItem == null)
            repository.law(bag)
    }
}
