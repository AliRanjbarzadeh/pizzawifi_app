package ir.atriatech.pizzawifi.ui.main.profile.address.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.model.LatLng
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.ARG_STRING_3
import ir.atriatech.core.ARG_STRING_4
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentAddressDetailBinding
import ir.atriatech.pizzawifi.entities.profile.address.Address


class AddressDetailFragment : BaseFragment() {
	lateinit var binding: FragmentAddressDetailBinding
	private lateinit var mViewModel: AddressDetailFragmentViewModel


	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(AddressDetailFragmentViewModel::class.java)

		try {
			arguments?.getParcelable<Address>(ARG_STRING_1)?.let {
				mViewModel.mItem = it
				mViewModel.name = it.name
				mViewModel.mobile = it.mobile
				mViewModel.address = it.address
			}
			arguments?.getParcelable<LatLng>(ARG_STRING_2)?.let {
				mViewModel.target = it
			}
			arguments?.getInt(ARG_STRING_3, -1)?.let {
				mViewModel.position = it
			}
			arguments?.getBoolean(ARG_STRING_4, false)?.let {
				mViewModel.isFromShopCart = it
			}
		} catch (e: Exception) {
		}
		mObserver()
	}

	override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_address_detail, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		hideKeyboard(binding.mainView)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.isFromShopCart) {
			mViewModel.mListShopCart = appDataBase.shopDao().getAll()
			if (mViewModel.mListShopCart.isEmpty()) {
				removeUntil(R.id.shopCartFragment)
				return
			}
		}
	}

	override fun handleNotification() {
		removeUntil(R.id.profileFragment)
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etName.clearFocus()
			binding.etMobile.clearFocus()
			binding.etAddress.clearFocus()
		}
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Msg> {
            override fun onProgress(loading: Boolean) {
	            setProgressView(binding.mainView, loading, type = 2)
            }

            override fun onSuccess(data: Msg) {
                val address = Address(
                    name = mViewModel.name,
                    mobile = mViewModel.mobile,
                    address = mViewModel.address,
                    lat = mViewModel.target.latitude,
                    lng = mViewModel.target.longitude
                )
                if (data.id > 0) {
                    address.id = data.id
                    baseFragmentCallback?.addItemToList(address)
                } else {
                    address.id = mViewModel.mItem.id
                    baseFragmentCallback?.editItemInList(address, mViewModel.position)
                }
                if (mViewModel.isFromShopCart) {
                    removeUntil(R.id.shopCartAddressFragment)
                } else {
                    removeUntil(R.id.addressFragment)
                }
            }

            override fun onFailed(msg: Msg) {
                when (msg.part) {
                    "area" -> {
                        MaterialDialog(requireContext())
                            .apply {
                                title(R.string.addressAddWarning)
                                message(text = msg.msg)
                                positiveButton(R.string._yes)
                                    .positiveButton {
                                        mViewModel.forceAdd = 1
                                        mViewModel.saveAddress()
                                    }
                                negativeButton(R.string.close)
                                show()
                            }
                    }

                    "area2" -> {
                        MaterialDialog(requireContext())
                            .apply {
                                title(R.string.addressAddWarning)
                                message(text = msg.msg)
                                positiveButton(R.string.close)
                                show()
                            }
                    }

                    else -> eToast(msg.msg)
                }
            }
        }, 5)
	}
}