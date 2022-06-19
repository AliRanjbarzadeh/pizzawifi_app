package ir.atriatech.pizzawifi.ui.login.step3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.BuildConfig
import ir.atriatech.core.BuildConfig.LOGIN_SESSION_KEY
import ir.atriatech.core.BuildConfig.TOKEN_SESSION_KEY
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.finish
import ir.atriatech.extensions.android.saveToSp
import ir.atriatech.extensions.android.showInputMethod
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.USER_INFO
import ir.atriatech.pizzawifi.common.WHICH_MENU_CLICKED
import ir.atriatech.pizzawifi.databinding.FragmentLoginStep3Binding
import ir.atriatech.pizzawifi.ui.login.LoginActivity

class LoginStep3Fragment : BaseFragment() {
	lateinit var binding: FragmentLoginStep3Binding
	private lateinit var mViewModel: LoginStep3FragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(LoginStep3FragmentViewModel::class.java)

		registerObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_step3, container, false)
		binding.lifecycleOwner = this
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		hideKeyboard(binding.mainView)
		view.post {
			showInputMethod(binding.etName)
		}
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etName.clearFocus()
			binding.etIntroducer.clearFocus()
		}
	}

	private fun registerObserver() {
		baseObserver(this, mViewModel.observerRegister, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading)
			}

			override fun onSuccess(data: Msg) {
				val bundle = Bundle()
				bundle.putString("mobile", AppObject.userInfo.mobile)
				baseFragmentCallback?.logFirebase("user_register", bundle)
				saveToSp(LOGIN_SESSION_KEY, true)

				//Save user token in session
				saveToSp(TOKEN_SESSION_KEY, data.token)

				//Save user in session
				AppObject.userInfo.name = mViewModel.name
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
		})
	}
}