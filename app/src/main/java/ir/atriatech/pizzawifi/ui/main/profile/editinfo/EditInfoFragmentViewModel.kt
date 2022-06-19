package ir.atriatech.pizzawifi.ui.main.profile.editinfo

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class EditInfoFragmentViewModel : BaseFragmentViewModel() {
    var name = AppObject.userInfo.name
    var birthDate = AppObject.userInfo.birthDate
    var weddingDate = AppObject.userInfo.weddingDate

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Msg>> by lazy { repository.editInfoOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun editInfo() {
        if (name.isEmpty()) {
            eToast(findString(R.string.emptyName))
            return
        }
        repository.editInfo(name, birthDate, weddingDate, bag)
    }
}
