package ir.atriatech.pizzawifi.ui.login.step3

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

class LoginStep3FragmentViewModel : BaseFragmentViewModel() {
	@Inject
	lateinit var repository: LoginRepository

	val observerRegister: LiveData<Outcome<Msg>> by lazy { repository.registerOutcome.toLiveData(bag) }

	init {
		component.inject(this)
	}

	var name = ""
	var introducer = ""

	fun register() {
		if (name.isEmpty()) {
			eToast(findString(R.string.emptyName))
			return
		}

		if (introducer.isNotEmpty() && !introducer.isMobile()) {
			eToast(findString(R.string.wrongMobile))
			return
		}

		repository.register(name, introducer, bag)
	}
}
