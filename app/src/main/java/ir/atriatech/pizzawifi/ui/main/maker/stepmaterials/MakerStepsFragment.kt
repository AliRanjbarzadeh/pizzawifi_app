package ir.atriatech.pizzawifi.ui.main.maker.stepmaterials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentMakerStepsBinding
import ir.atriatech.pizzawifi.entities.home.maker.Step
import ir.atriatech.pizzawifi.ui.main.maker.MakerStep3Fragment

class MakerStepsFragment : BaseFragment() {
	lateinit var binding: FragmentMakerStepsBinding
	private lateinit var mViewModel: MakerStepsFragmentViewModel

	companion object {
		fun newInstance(step: Step, isHalf: Boolean): MakerStepsFragment {
			val makerStepsFragment = MakerStepsFragment()
			makerStepsFragment.arguments = extraArguments(argument1 = step, key1 = ARG_STRING_1, argument2 = isHalf, key2 = ARG_STRING_2)

			return makerStepsFragment
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(MakerStepsFragmentViewModel::class.java)
		try {
			arguments?.getParcelable<Step>(ARG_STRING_1)?.let { mViewModel.mItem.set(it) }
			arguments?.getBoolean(ARG_STRING_2)?.let { isHalf: Boolean -> mViewModel.isHalf = isHalf }
		} catch (ex: Exception) {
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maker_steps, container, false)

		binding.lifecycleOwner = this
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setAdapter()
	}

	private fun setAdapter() {
		if (mViewModel.checkIsHalf()) {
			//Right materials
			if (mViewModel.mRightAdapter == null) {
				mViewModel.mRightAdapter =
					MakerStepMaterialsAdapter((parentFragment as MakerStep3Fragment).makerRightActions, true)
				mViewModel.mRightAdapter!!.addToList(mViewModel.mItem.get()!!.rightMaterials.toMutableList())
			}
			binding.rvRightMaterials.setHasFixedSize(true)
			binding.rvRightMaterials.setItemViewCacheSize(mViewModel.mItem.get()!!.rightMaterials.size)
			binding.rvRightMaterials.layoutManager = GridLayoutManager(requireContext(), 1)
			binding.rvRightMaterials.adapter = mViewModel.mRightAdapter

			//Left materials
			if (mViewModel.mLeftAdapter == null) {
				mViewModel.mLeftAdapter =
					MakerStepMaterialsAdapter((parentFragment as MakerStep3Fragment).makerLeftActions, true)
				mViewModel.mLeftAdapter!!.addToList(mViewModel.mItem.get()!!.leftMaterials.toMutableList())
			}
			binding.rvLeftMaterials.setHasFixedSize(true)
			binding.rvLeftMaterials.setItemViewCacheSize(mViewModel.mItem.get()!!.leftMaterials.size)
			binding.rvLeftMaterials.layoutManager = GridLayoutManager(requireContext(), 1)
			binding.rvLeftMaterials.adapter = mViewModel.mLeftAdapter
		} else {
			if (mViewModel.mAdapter == null) {
				mViewModel.mAdapter =
					MakerStepMaterialsAdapter((parentFragment as MakerStep3Fragment).makerActions)
				mViewModel.mAdapter!!.addToList(mViewModel.mItem.get()!!.materials.toMutableList())
			}

			binding.rvMakerSteps.setHasFixedSize(true)
			binding.rvMakerSteps.setItemViewCacheSize(mViewModel.mItem.get()!!.materials.size)
//        binding.rvMakerSteps.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
			binding.rvMakerSteps.layoutManager =
				GridLayoutManager(requireContext(), 2)
			binding.rvMakerSteps.adapter = mViewModel.mAdapter
		}
	}
}