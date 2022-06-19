package ir.atriatech.pizzawifi.ui.main.profile.editinfo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.android.sToast
import ir.atriatech.extensions.android.saveToSp
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.USER_INFO
import ir.atriatech.pizzawifi.databinding.FragmentEditInfoBinding
import kotlinx.android.synthetic.main.fragment_edit_info.*

class EditInfoFragment : BaseFragment() {
	lateinit var binding: FragmentEditInfoBinding
	private lateinit var mViewModel: EditInfoFragmentViewModel
	private var mDatePickerDialog: DatePickerDialogFragment? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(EditInfoFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_info, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		hideKeyboard(binding.mainView)

		binding.etBirrthDate.setOnClickListener {
			mDatePickerDialog = DatePickerDialogFragment()
			mDatePickerDialog!!.setTargetFragment(this, 1)
			mDatePickerDialog!!.show(parentFragmentManager, "BirthDate")
		}

		binding.etWeddingDate.setOnClickListener {
			mDatePickerDialog = DatePickerDialogFragment()
			mDatePickerDialog!!.setTargetFragment(this, 2)
			mDatePickerDialog!!.show(parentFragmentManager, "WeddingDate")
		}
	}

	override fun handleNotification() {
		mDatePickerDialog?.dismiss()
		removeUntil(R.id.profileFragment)
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etName.clearFocus()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
//        binding.btnSend.dispose()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (resultCode == RESULT_OK) {
			when (requestCode) {
				1 -> {//Birth date
					data?.extras?.let {
						binding.etBirrthDate.setText(it.getString("date"))
					}
				}


				2 -> {//Wedding date
					data?.extras?.let {
						binding.etWeddingDate.setText(it.getString("date"))
					}
				}

			}
		}
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
//                binding.btnSend.startAnimation()
				setProgressView(mainView, loading)
			}

			override fun onSuccess(data: Msg) {
//                binding.btnSend.revertAnimation()
				sToast(data.msg)
				AppObject.userInfo.name = mViewModel.name
				AppObject.userInfo.birthDate = mViewModel.birthDate
				AppObject.userInfo.weddingDate = mViewModel.weddingDate
				saveToSp(USER_INFO, AppObject.userInfo)
				back()
			}

			override fun onFailed(errorMessage: String) {
//                binding.btnSend.revertAnimation()
			}
		}, 1)
	}
}