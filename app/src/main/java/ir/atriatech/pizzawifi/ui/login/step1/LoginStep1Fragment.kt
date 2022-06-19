package ir.atriatech.pizzawifi.ui.login.step1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.android.showInputMethod
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.isMobile
import ir.atriatech.extensions.kotlin.toEnglish
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentLoginStep1Binding
import kotlinx.android.synthetic.main.fragment_login_step1.*


class LoginStep1Fragment : BaseFragment() {
	lateinit var binding: FragmentLoginStep1Binding
	private lateinit var mViewModel: LoginStep1FragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(LoginStep1FragmentViewModel::class.java)
		loginObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_step1, container, false)
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel


		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		hideKeyboard(binding.mainView)

		view.post {
			showInputMethod(etMobile)
		}

		binding.etMobile.setOnEditorActionListener { _, actionId, _ ->
			var keyAction = false
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				when {
					mViewModel.mobile.isMobile() -> btnNext.callOnClick()
					mViewModel.mobile.isEmpty() -> {
						eToast(findString(R.string.emptyMobile))
						keyAction = true
					}
					else -> {
						eToast(findString(R.string.wrongMobile))
						keyAction = true
					}
				}
			}
			keyAction
		}
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etMobile.clearFocus()
		} else {
			binding.etMobile.requestFocus()
			binding.mainNested.fullScroll(View.FOCUS_DOWN)
		}
	}

	private fun loginObserver() {
		baseObserver(this, mViewModel.loginObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(mainView, loading)
			}

			override fun onSuccess(data: Msg) {
				//Send code by sms
				mViewModel.sendCode()
				AppObject.userInfo.mobile = mViewModel.mobile

				val arg = extraArguments(mViewModel.mobile.trim().toEnglish(), ARG_STRING_1)
				navTo(R.id.loginStep2Fragment, arg = arg)
			}
		})
	}
}