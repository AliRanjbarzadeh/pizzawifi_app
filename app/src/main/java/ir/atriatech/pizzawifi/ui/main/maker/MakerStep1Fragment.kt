package ir.atriatech.pizzawifi.ui.main.maker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentMakerStep1Binding
import ir.atriatech.pizzawifi.entities.home.maker.Maker


class MakerStep1Fragment : BaseFragment() {
	lateinit var binding: FragmentMakerStep1Binding
	private lateinit var mViewModel: MakerStep1FragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(MakerStep1FragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maker_step1, container, false)
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		mViewModel.getData()

		//Full pizza
		binding.imgFullPizza.setOnClickListener { goToFullPizza() }
		binding.cvFullPizza.setOnClickListener { goToFullPizza() }

		//Half pizza
		binding.imgHalfPizza.setOnClickListener { goToHalfPizza() }
		binding.cvHalfPizza.setOnClickListener { goToHalfPizza() }
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Maker> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Maker) {
				mViewModel.mItem.set(data)
				mViewModel.isLoaded = true
				d(data, "MakerData")
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		}, 4)
	}

	private fun goToFullPizza() {
		val arg = extraArguments(argument1 = mViewModel.mItem.get()!!, key1 = ARG_STRING_1, argument2 = false, key2 = ARG_STRING_2)
		navTo(R.id.makerStep2Fragment, arg = arg)
	}

	private fun goToHalfPizza() {
		val arg = extraArguments(argument1 = mViewModel.mItem.get()!!, key1 = ARG_STRING_1, argument2 = true, key2 = ARG_STRING_2)
		navTo(R.id.makerStep2Fragment, arg = arg)
	}
}