package ir.atriatech.pizzawifi.ui.main.profile.campaign.register

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentCampaignRegisterBinding
import ir.atriatech.pizzawifi.entities.profile.campaign.CampaignDemo

class CampaignRegisterFragment : BaseFragment() {
	lateinit var binding: FragmentCampaignRegisterBinding
	private lateinit var mViewModel: CampaignRegisterFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(CampaignRegisterFragmentViewModel::class.java)
		mViewModel.id = requireArguments().getInt(ARG_STRING_1)
		mViewModel.instagram = AppObject.userInfo.instagram
		mObserver()
		mObserverSave()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_campaign_register, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.campaignObject.name.isNotEmpty()) {
			setData()
		}
		mViewModel.getData()

	}


	private fun setData() {
		binding.txtTitle.text = mViewModel.campaignObject.name
		binding.txtStartCampaign.text = mViewModel.campaignObject.startedAt
		binding.txtEndCampaign.text = mViewModel.campaignObject.endedAt
		binding.txt3.text = mViewModel.campaignObject.uploadDescription
		binding.imageViewCampaign.setOnClickListener { pickImage(size = 2000, aspectY = 16, aspectX = 9) }

		if (AppObject.userInfo.instagram.isNotEmpty()) {
			binding.etInstagram.isEnabled = false
			binding.etInstagram.isFocusable = false
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			binding.txtDescription.text = Html.fromHtml(mViewModel.campaignObject.description, Html.FROM_HTML_MODE_COMPACT)
		} else {
			binding.txtDescription.text = Html.fromHtml(mViewModel.campaignObject.description)
		}
		imageLoader.load(
			mViewModel.campaignObject.image.getImageUri(),
			binding.image, false
		)

	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<CampaignDemo> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, type = 2)
			}

			override fun onSuccess(data: CampaignDemo) {
				mViewModel.isShowContent.set(true)

				mViewModel.campaignObject.initialize(data)
				setData()
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		})
	}

	private fun mObserverSave() {
		baseObserver(this, mViewModel.mObserverSave, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading)
			}

			override fun onSuccess(data: Msg) {
				sToast(data.msg)
				back()
			}
		})
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		pickImageOnActivityResult(requestCode, data, object : PickImageCallback {
			override fun onSuccess(uri: Uri) {
				binding.imageViewCampaign.setImageURI(uri)
				mViewModel.uri = uri
			}

			override fun onFail(error: Exception) {
				eToast(findString(R.string.wrongImageValue))
			}
		})
	}
}