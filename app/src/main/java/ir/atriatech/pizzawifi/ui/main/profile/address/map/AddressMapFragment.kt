package ir.atriatech.pizzawifi.ui.main.profile.address.map

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.ARG_STRING_3
import ir.atriatech.core.ARG_STRING_4
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.afterDelayTextChanged
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MyItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentAddressMapBinding
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.profile.address.AddressSearch
import ir.atriatech.utill.others.map.LocationProvider
import ir.atriatech.utill.others.map.LocationProviderListener
import kotlinx.android.synthetic.main.fragment_address_map.*


class AddressMapFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentAddressMapBinding
	private lateinit var mViewModel: AddressMapFragmentViewModel
	private var locationProvider: LocationProvider? = null
	private var position = -1
	private var isFromShopCart = false
	private var permissionDisposable: Disposable? = null
	private var isFirstTime = true
	var lat = 29.6679769
	var lng = 52.4562958

	//region Map
	private lateinit var mapView: SupportMapFragment
	private var mGoogleMap: GoogleMap? = null
	private var isLocationClick = false
	//endregion

	init {
		component.inject(this)
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(AddressMapFragmentViewModel::class.java)

		try {
			arguments?.getParcelable<Address>(ARG_STRING_1)?.let {
				mViewModel.mItem = it
			}
			arguments?.getInt(ARG_STRING_2, -1)?.let {
				position = it
				mViewModel.position = it
			}
			arguments?.getBoolean(ARG_STRING_3, false)?.let {
				isFromShopCart = it
			}
		} catch (e: Exception) {
		}
		if (position >= 0) {
			isFirstTime = false
		}
		mSearchObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_map, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (isFromShopCart) {
			mViewModel.mListShopCart = appDataBase.shopDao().getAll()
			if (mViewModel.mListShopCart.isEmpty()) {
				removeUntil(R.id.shopCartFragment)
				return
			}
		}

		showMap()
		binding.fabLocation.setOnClickListener {
			isLocationClick = true
			locationProvider?.getLocation()
		}

		binding.btnNext.setOnClickListener {
			mGoogleMap?.let {
				if (isFromShopCart) {
					val arg = extraArguments(
						argument1 = mViewModel.mItem,
						key1 = ARG_STRING_1,
						argument2 = it.cameraPosition.target,
						key2 = ARG_STRING_2,
						argument3 = position,
						key3 = ARG_STRING_3,
						argument4 = true,
						key4 = ARG_STRING_4
					)
					navTo(R.id.addressDetailFragment2, arg = arg)
				} else {
					val arg = extraArguments(
						argument1 = mViewModel.mItem,
						key1 = ARG_STRING_1,
						argument2 = it.cameraPosition.target,
						key2 = ARG_STRING_2,
						argument3 = position,
						key3 = ARG_STRING_3
					)
					navTo(R.id.addressDetailFragment, arg = arg)
				}
			}
		}

		binding.etSearch.afterDelayTextChanged({
			mViewModel.mListItems.clear()
			mViewModel.mAdapter?.list?.clear()
			mViewModel.mAdapter?.notifyDataSetChanged()
			binding.rvPlaces.removeAllViews()
			if (it.isNotEmpty()) {
				binding.rvPlaces.visibility = View.VISIBLE
				mViewModel.search()
			} else {
				binding.rvPlaces.visibility = View.GONE
			}
		}, time = 400, activity = requireActivity())


		binding.imgMic.setOnClickListener {
			if (mViewModel.viewModelLoading.get()) {
				return@setOnClickListener
			}
			val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(
				RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
			);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, AppObject.language)
			intent.putExtra(
				RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt)
			)
			try {
				startActivityForResult(intent, mViewModel.MICROPHONE_REQUEST_CODE)
			} catch (a: ActivityNotFoundException) {
				eToast(findString(R.string.SorryYourDeviceDoesNotSupportSpeechInput))
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		locationProvider?.onActivityResult(requestCode, resultCode, data)
		if (requestCode == mViewModel.MICROPHONE_REQUEST_CODE) {
			when (resultCode) {
				Activity.RESULT_OK -> {
					val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
					d("result value ${results}", "TAG")
					if (results != null && results.isNotEmpty()) {
						binding.etSearch.setText(results[0].toString())
					}
				}
			}
		}

	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etSearch.clearFocus()
		}
	}

	override fun handleNotification() {
		removeUntil(R.id.profileFragment)
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as AddressSearch
		val cameraPosition =
			CameraPosition.Builder()
				.zoom(16f)
				.target(LatLng(item.lat, item.lng))
				.build()

		mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		mViewModel.mAdapter?.notifyDataSetChanged()
		binding.rvPlaces.removeAllViews()
		binding.rvPlaces.setBackgroundColor(findColor(R.color.transparent))
		binding.etSearch.setText(null)
	}

	override fun onResume() {
		super.onResume()
		baseFragmentCallback?.changeSoftkeyMethod(true)
	}

	override fun onDestroyView() {
		baseFragmentCallback?.changeSoftkeyMethod(false)
		permissionDisposable?.dispose()
		super.onDestroyView()
	}

	fun showMap() {
		try {
			mapView = SupportMapFragment.newInstance()
			childFragmentManager.beginTransaction()
				.replace(R.id.frameMap, mapView)
				.commit()
			mapView.getMapAsync(onMapReady)
		} catch (ex: Exception) {
			e(ex.message, "MapFragment")
		}
	}

	/**
	 * On map ready
	 */
	private val onMapReady = OnMapReadyCallback { googleMap ->
		mGoogleMap = googleMap
		val target: LatLng = if (mViewModel.mItem.id > 0) {
			LatLng(mViewModel.mItem.lat, mViewModel.mItem.lng)
		} else {
			LatLng(29.6679769, 52.4562958)
		}

		val cameraPosition =
			CameraPosition.Builder()
				.zoom(16f)
				.target(target)
				.build()

		mGoogleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

		mGoogleMap?.let {
			val uiSettings = it.uiSettings
			uiSettings.isMyLocationButtonEnabled = false
			uiSettings.isCompassEnabled = true
			uiSettings.isZoomControlsEnabled = false
			uiSettings.isIndoorLevelPickerEnabled = false
			uiSettings.isRotateGesturesEnabled = false
			uiSettings.isTiltGesturesEnabled = false
		}

		/*  permissionDisposable?.let { it.dispose() }
		  permissionDisposable =
			  RxPermissions(this).request(
				  Manifest.permission.ACCESS_FINE_LOCATION,
				  Manifest.permission.ACCESS_COARSE_LOCATION
			  )
				  .subscribe {
					  if (it) {
						  mGoogleMap?.isMyLocationEnabled = true
					  } else {
						  isLocationClick = false
					  }
				  }
  */

		if (RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
			mGoogleMap?.isMyLocationEnabled = true
		}
		mGoogleMap?.setOnCameraMoveListener { hideInputMethod() }
		mGoogleMap?.setOnMapClickListener { hideInputMethod() }
		locationObserver()

		mGoogleMap?.let {
			//***************************  Animation *******************

//                transition down
//                ObjectAnimator.ofFloat(imgMarker, "translationY", (flMap.height/2- txtPreviousAddress.height).toFloat()).apply {
//                    duration = 500
//                    start()
//                }
//            if (!mViewModel.firstTimeMarkerAnim) {

			ObjectAnimator.ofFloat(fabLocation, "alpha", 0f, 1f).apply {
				duration = 1000
				start()
			}

			val considerHeight = (frameMap.height / 2 - imgMarker.height).toFloat()
			imgMarker.animate().alpha(1f)
			imgMarker.animate()
				.y((frameMap.height / 2 - imgMarker.height).toFloat())
				.setDuration(500).setStartDelay(1)
				.setListener(object : Animator.AnimatorListener {
					override fun onAnimationStart(animation: Animator?) {
						d("TAG", "Animation Start")
					}

					override fun onAnimationRepeat(animation: Animator?) {
						d("TAG", "Animation Repeat")
					}

					override fun onAnimationEnd(animation: Animator?) {
						if (imgMarker != null && imgMarker.animate() != null) {

							imgMarker?.animate()!!.y((considerHeight) - 60f)
								.setListener(object : Animator.AnimatorListener {
									override fun onAnimationRepeat(p0: Animator?) {
									}

									override fun onAnimationEnd(p0: Animator?) {
										imgMarker?.animate()?.y((considerHeight) + 18f)
										mViewModel.firstTimeMarkerAnim = true

									}

									override fun onAnimationCancel(p0: Animator?) {

									}

									override fun onAnimationStart(p0: Animator?) {

									}

								})
						}

					}

					override fun onAnimationCancel(animation: Animator?) {
						d("TAG", "Animation Cancel")
					}
				})

//            }
			//***************************  Animation *******************
		}
	}

	private fun locationObserver() {
		if (locationProvider == null) {
			locationProvider = LocationProvider(this, object : LocationProviderListener {
				override fun onLocationDetect(location: Location) {
					if (mGoogleMap?.isMyLocationEnabled != true)
						mGoogleMap?.isMyLocationEnabled = true

					if (isLocationClick || isFirstTime) {
						val cameraPosition =
							CameraPosition.Builder()
								.zoom(16f)
								.target(LatLng(location.latitude, location.longitude))
								.build()

						if (isFirstTime || isLocationClick)
							mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

						isLocationClick = false
						isFirstTime = false
					}
				}
			})
		}
		locationProvider?.getLocation()
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = AddressSearchAdapter(this)
		} else {
			try {
				binding.rvPlaces.removeItemDecorationAt(0)
			} catch (ex: Exception) {
			}
		}
		mViewModel.mAdapter!!.addToList(mViewModel.mListItems)

		binding.rvPlaces.setHasFixedSize(true)
		binding.rvPlaces.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		binding.rvPlaces.addItemDecoration(MyItemDecoration(findColor(R.color.colorD4), dp2px(1)))
		binding.rvPlaces.adapter = mViewModel.mAdapter
	}

	private fun mSearchObserver() {
		baseObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<AddressSearch>> {
				override fun onProgress(loading: Boolean) {
					if (loading) {
						binding.rvPlaces.showShimmerAdapter()
						binding.rvPlaces.setBackgroundColor(findColor(R.color.appBackgroundColor))
					} else {
						binding.rvPlaces.setBackgroundColor(findColor(R.color.transparent))
						binding.rvPlaces.hideShimmerAdapter()
					}
				}

				override fun onSuccess(data: MutableList<AddressSearch>) {
					mViewModel.mListItems = data
					setAdapter()
				}

				override fun onFailed(errorMessage: String) {
				}
			},
			1
		)
	}
}