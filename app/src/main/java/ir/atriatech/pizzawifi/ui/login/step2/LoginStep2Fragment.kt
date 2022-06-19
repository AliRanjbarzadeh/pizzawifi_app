package ir.atriatech.pizzawifi.ui.login.step2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.BuildConfig.*
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.base.TimerObserver
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.afterTextChanged
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.base.startTimer
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.USER_INFO
import ir.atriatech.pizzawifi.common.WHICH_MENU_CLICKED
import ir.atriatech.pizzawifi.databinding.FragmentLoginStep2Binding
import ir.atriatech.pizzawifi.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_login_step2.*


class LoginStep2Fragment : BaseFragment() {
	lateinit var binding: FragmentLoginStep2Binding
	private lateinit var mViewModel: LoginStep2FragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(LoginStep2FragmentViewModel::class.java)

		try {
			mViewModel.mobile = requireArguments().getString(ARG_STRING_1, "")
		} catch (e: Exception) {
		}

		verifyObserver()
		resendObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_step2, container, false)
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		hideKeyboard(binding.mainView)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.viewModel = mViewModel

		//Verify code
		binding.etCode.afterTextChanged {
			if (it.length == 5) {
				mViewModel.verifyCode()
			}
		}
		binding.etCode.setOnEditorActionListener { _, actionId, _ ->
			var keyAction = false
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				when {
					mViewModel.code.length == 5 -> mViewModel.verifyCode()
					mViewModel.code.isEmpty() -> {
						eToast(findString(R.string.enterCode))
						keyAction = true
					}
					else -> {
						eToast(findString(R.string.wrongCode))
						keyAction = true
					}
				}
			}
			keyAction
		}

		view.post {
			showInputMethod(etCode)
		}

		startTimer()
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etCode.clearFocus()
		} else {
			binding.etCode.requestFocus()
		}
	}

	private fun startTimer() {
		mViewModel.startTimer(object : TimerObserver {
			override fun onEmit(time: String) {
				mViewModel.showSendAgain.set(false)
				mViewModel.timerText.set(time)
			}

			override fun onComplete() {
				mViewModel.showSendAgain.set(true)
			}

		})
	}

	private fun resendObserver() {
		baseObserver(this, mViewModel.observerResendCode, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(mainView, loading)
			}

			override fun onSuccess(data: Msg) {
				startTimer()
			}
		})
	}

	//Set code from sms
	fun setCode(code: String?) {
		binding.etCode.setText(code)
	}


	private fun verifyObserver() {
		baseObserver(this, mViewModel.observerVerifyCode, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(mainView, loading)
			}

			override fun onSuccess(data: Msg) {
				AppObject.userInfo.token = data.token
				if (data.name.isEmpty()) {
					saveToSp(TOKEN_REGISTER_KEY, data.token)
					navTo(R.id.loginStep3Fragment)
				} else {
					saveToSp(TOKEN_SESSION_KEY, data.token)
					val bundle = Bundle()
					bundle.putString("mobile", AppObject.userInfo.mobile)
					baseFragmentCallback?.logFirebase("user_login", bundle)

					saveToSp(LOGIN_SESSION_KEY, true)

					//Save user in session
					AppObject.userInfo.name = data.name
					AppObject.userInfo.birthDate = data.birthDate
					AppObject.userInfo.weddingDate = data.weddingDate
					AppObject.userInfo.instagram = data.instagram
					AppObject.userInfo.wallet = data.wallet
					AppObject.userInfo.club = data.club
					AppObject.userInfo.isLogin = true
					saveToSp(USER_INFO, AppObject.userInfo)

					var whichMenu = -1
					if (requireActivity() is LoginActivity) {
						whichMenu = (requireActivity() as LoginActivity).whichMenu
					}
					//set result for main activity and finish activity
					val resultIntent = Intent()
					resultIntent.apply {
						putExtra("login", true)
						putExtra(WHICH_MENU_CLICKED, whichMenu)
					}
					requireActivity().setResult(Activity.RESULT_OK, resultIntent)
					finish()
				}
			}
		})
	}
}