package ir.atriatech.pizzawifi.ui.main.profile.campaign.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_PARCELABLE_1
import ir.atriatech.extensions.android.gone
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentCampaignDetailBinding

class CampaignDetailFragment : BaseFragment() {
	lateinit var binding: FragmentCampaignDetailBinding
	private lateinit var mViewModel: CampaignDetailFragmentViewModel
	init {
		component.inject(this)
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(CampaignDetailFragmentViewModel::class.java)
		try {
			mViewModel.mItem = requireArguments().getParcelable(ARG_PARCELABLE_1)!!
		} catch (e: Exception) {
		}

	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_campaign_detail, container, false)
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setData() }


	private fun setData() {
		binding.viewStatus.setCardBackgroundColor(Color.parseColor(mViewModel.mItem.statusColor))
		d(mViewModel.mItem.image.getImageUri("",true),"myImage")
        imageLoader.load(mViewModel.mItem.image.getImageUri("",true),
            binding.image, false)
		if (mViewModel.mItem.declineReason.isNullOrEmpty()) {
			binding.card3.gone()
		} else {
			binding.card3.setCardBackgroundColor(Color.parseColor(mViewModel.mItem.statusColor))
			binding.txtDecline.text=mViewModel.mItem.declineReason
		}

	}
}