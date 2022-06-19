package ir.atriatech.pizzawifi.ui.login.step1

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.isMobile
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.repository.LoginRepository
import javax.inject.Inject

class LoginStep1FragmentViewModel : BaseFragmentViewModel() {
	@Inject
	lateinit var repository: LoginRepository

	val loginObserver: LiveData<Outcome<Msg>> by lazy { repository.msgOutcome.toLiveData(bag) }

	init {
		component.inject(this)
	}

	var mobile = ""

	fun login() {
		if (mobile.isEmpty()) {
			eToast(findString(R.string.emptyMobile))
			return
		}

		if (!mobile.isMobile()) {
			eToast(findString(R.string.wrongMobile))
			return
		}

		repository.login(mobile, bag)
	}

	fun sendCode() {
		repository.sendCode(mobile, bag)
	}
}
