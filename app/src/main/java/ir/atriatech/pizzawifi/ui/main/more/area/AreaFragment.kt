package ir.atriatech.pizzawifi.ui.main.more.area

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tbruyelle.rxpermissions2.RxPermissions
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.SELECTED_CITY_MODEL
import ir.atriatech.pizzawifi.databinding.FragmentAreaBinding
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.more.area.AreaItem


class AreaFragment : BaseFragment() {
	lateinit var binding: FragmentAreaBinding
	private lateinit var mViewModel: AreaFragmentViewModel

	//region Map
	private lateinit var mapView: SupportMapFragment
	private var mGoogleMap: GoogleMap? = null
	//endregion

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(AreaFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_area, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.getData()) {
			showMap()
		}
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
		val target = LatLng(29.6679769, 52.4562958)
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
			uiSettings.isZoomControlsEnabled = true
			uiSettings.isIndoorLevelPickerEnabled = false
			uiSettings.isRotateGesturesEnabled = false
			uiSettings.isTiltGesturesEnabled = false
		}
		if (RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
			mGoogleMap?.isMyLocationEnabled = true
		}
		drawAreas()
	}

	fun drawAreas() {
		val bounds = LatLngBounds.Builder()
		val cityBound = LatLngBounds.Builder()
		val cityModel = loadFromSp<CityModel>(SELECTED_CITY_MODEL, CityModel())

		mViewModel.mListItems.forEach { mAreaItem ->
			val polygonOptions = PolygonOptions()
			polygonOptions.strokeColor(Color.parseColor(mAreaItem.strokeColor.replace("#", "#59")))
			polygonOptions.fillColor(Color.parseColor(mAreaItem.innerColor.replace("#", "#59")))

			mAreaItem.area.forEach { mArea ->
				val latLng = LatLng(mArea.lat, mArea.lng)
				polygonOptions.add(latLng)
				bounds.include(latLng)
				if (mAreaItem.cityId == cityModel.id) {
					cityBound.include(latLng)
				}
			}
			mGoogleMap?.addPolygon(polygonOptions)
		}

		val cameraUpdate = if (cityModel.id > 0) {
			CameraUpdateFactory.newLatLngBounds(cityBound.build(), dp2px(20))
		} else {
			CameraUpdateFactory.newLatLngBounds(bounds.build(), dp2px(20))
		}

		/*
		val center = bounds.build().center
		val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(center.latitude, center.longitude), dp2px(20).toFloat())
		 */
		mGoogleMap?.moveCamera(cameraUpdate)
	}

	private fun mObserver() {
		baseObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<AreaItem>> {
				override fun onProgress(loading: Boolean) {
					setProgressView(binding.mainView, loading, type = 2)
				}

				override fun onSuccess(data: MutableList<AreaItem>) {
					mViewModel.mListItems = data
					showMap()
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.getData()
				}
			},
			4
		)
	}
}