package ir.atriatech.pizzawifi.ui.main.profile.changemobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.base.TimerObserver
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.afterTextChanged
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.base.startTimer
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.USER_INFO
import ir.atriatech.pizzawifi.databinding.FragmentChangeMobileStep2Binding
import kotlinx.android.synthetic.main.fragment_change_mobile_step2.*

class ChangeMobileStep2Fragment : BaseFragment() {
	lateinit var binding: FragmentChangeMobileStep2Binding
	private lateinit var mViewModel: ChangeMobileStep2FragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ChangeMobileStep2FragmentViewModel::class.java)

		try {
			arguments?.getString(ARG_STRING_1, "")?.let { mobile ->
				mViewModel.mobile = mobile
			}
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
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_change_mobile_step2,
			container,
			false
		)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

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
			showInputMethod(binding.etCode)
		}
		startTimer()
	}

	override fun handleNotification() {
		removeUntil(R.id.profileFragment)
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


	fun setCode(code: String?) {
		if (code == null)
			return

		binding.etCode.setText(code)
		mViewModel.code = code
		mViewModel.verifyCode()
	}


	private fun verifyObserver() {
		baseObserver(this, mViewModel.observerVerifyCode, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(mainView, loading)
			}

			override fun onSuccess(data: Msg) {
				AppObject.userInfo.mobile = mViewModel.mobile
				saveToSp(USER_INFO, AppObject.userInfo)
				findNavController().navigateUp()
				back()
			}
		})
	}
}