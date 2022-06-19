package ir.atriatech.pizzawifi.ui.main.profile.changemobile

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.isMobile
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ChangeMobileStep1FragmentViewModel : BaseFragmentViewModel() {
    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Msg>> by lazy { repository.changeNumberOutcome.toLiveData(bag) }

    var mobile = ""

    init {
        component.inject(this)
    }

    fun changeNumber() {
        if (mobile.isEmpty()) {
            eToast(findString(R.string.emptyMobile))
            return
        }
        if (!mobile.isMobile()) {
            eToast(findString(R.string.wrongMobile))
            return
        }
        if (mobile.equals(AppObject.userInfo.mobile)) {
            eToast(findString(R.string.sameNumber))
            return
        }
        repository.changeNumber(mobile, bag)
    }

    fun sendCode() {
        repository.sendCode(mobile, bag)
    }
}
