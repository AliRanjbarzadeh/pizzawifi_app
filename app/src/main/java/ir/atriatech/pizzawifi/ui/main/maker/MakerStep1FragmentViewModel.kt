package ir.atriatech.pizzawifi.ui.main.maker

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.entities.home.maker.Maker
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class MakerStep1FragmentViewModel : BaseFragmentViewModel() {
    var mItem = ObservableField<Maker>()
    var isLoaded = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Maker>> by lazy { repository.makerOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (!isLoaded) {
            repository.maker(AppObject.selectedBranchId, bag)
            return false
        }
        return true
    }
}
