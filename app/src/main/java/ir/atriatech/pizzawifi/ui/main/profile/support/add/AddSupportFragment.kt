package ir.atriatech.pizzawifi.ui.main.profile.support.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.NOTIFICATION_KEY
import ir.atriatech.pizzawifi.databinding.FragmentAddSupportBinding
import ir.atriatech.pizzawifi.entities.profile.support.Support


class AddSupportFragment : BaseFragment() {
	lateinit var binding: FragmentAddSupportBinding
	private lateinit var mViewModel: AddSupportFragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(AddSupportFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_support, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		hideKeyboard(binding.mainView)
		return binding.root
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		when (key) {
			"support" -> {
				removeUntil(R.id.supportFragment)
			}

			"orders" -> {
			}

			"" -> {
			}

			else -> removeUntil(R.id.profileFragment)
		}
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etTitle.clearFocus()
			binding.etMessage.clearFocus()
		}
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Support> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, type = 2)
			}

			override fun onSuccess(data: Support) {
				baseFragmentCallback?.addItemToList(data)
				mViewModel.sendNotice(data.id)
				back()
			}

			override fun onFailed(errorMessage: String) {
			}
		})
	}
}