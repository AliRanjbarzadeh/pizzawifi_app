package ir.atriatech.pizzawifi.ui.main.profile.changemobile

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.toEnglish
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ChangeMobileStep2FragmentViewModel : BaseFragmentViewModel() {
    @Inject
    lateinit var repository: MainRepository

    val observerResendCode: LiveData<Outcome<Msg>> by lazy {
        repository.resendCodeOutcome.toLiveData(
            bag
        )
    }
    val observerVerifyCode: LiveData<Outcome<Msg>> by lazy {
        repository.verifyCodeOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    var showSendAgain: ObservableField<Boolean> = ObservableField(false)
    var mobile = ""
    var code = ""
    var timerText = ObservableField<String>("02:00")

    fun resendCode() {
        repository.resendCode(mobile, bag)
    }

    fun verifyCode() {
        if (code.isEmpty()) {
            eToast(findString(R.string.enterCode))
            return
        }
        repository.verifyCode(mobile, code.trim().toEnglish(), bag)
    }
}
