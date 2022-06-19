package ir.atriatech.utill.others.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.*
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.LOCATION_PROVIDER_GPS_RESULT
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.extensions.reactivex.addTo
import ir.atriatech.util.R

class LocationProvider(
	private val fragment: Fragment,
	var locationProviderListener: LocationProviderListener
) {
	private var locationRequest: LocationRequest = LocationRequest()
	private var rxLocation: RxLocation
	private var disposable = CompositeDisposable()
	private val rxPermissions = RxPermissions(fragment.requireActivity())


	init {
		locationRequest.run {
			interval = 10000
			fastestInterval = 5000
			priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		}
		rxLocation = RxLocation(fragment.requireContext())
	}

	fun getLocation() {
		disposable = CompositeDisposable()
		onPermissionLocationRequest()
	}

	fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			LOCATION_PROVIDER_GPS_RESULT -> {
				when (resultCode) {
					Activity.RESULT_OK -> {
						makeFusedLocation()
					}
					else -> locationProviderListener.onAccessDenied()
				}
			}
		}
	}

	private fun startLocationListening() {
		val mHighAccuracy = LocationRequest()
		mHighAccuracy.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		mHighAccuracy.interval = 5000

		val mBalancedAccuracy = LocationRequest()
		mBalancedAccuracy.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

		val builder = LocationSettingsRequest
			.Builder()
			.addLocationRequest(mHighAccuracy)
			.addLocationRequest(mBalancedAccuracy)
			.setNeedBle(true)

		val client: SettingsClient = LocationServices.getSettingsClient(fragment.requireActivity())
		val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
		task.addOnSuccessListener(locationSettingSuccess)
		task.addOnFailureListener(locationSettingFailure)
	}

	private val locationSettingSuccess = OnSuccessListener<LocationSettingsResponse> { makeFusedLocation() }

	private val locationSettingFailure = OnFailureListener { ex ->
		when (ex) {
			is ApiException -> {
				when (ex.statusCode) {
					LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
						try {
							val resolvable = ex as ResolvableApiException
							resolvable.startResolutionForResult(fragment.activity, LOCATION_PROVIDER_GPS_RESULT)
						} catch (e1: Exception) {
							e(e1.message)
						}
					}

					else -> {
						eToast("خطا در دریافت مکان ")
					}
				}
			}
			else -> {
				eToast("خطا در دریافت مکان")
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun makeFusedLocation() {
		rxLocation.location()
			.updates(locationRequest)
			.subscribe({
				if (it.latitude != 0.0) {
					locationProviderListener.onLocationDetect(it)
					disposable.clear()
					disposable.dispose()
				}
			}, {
				d(it)
			}).addTo(disposable)
	}

	private fun displayRationalDialog() {
		MaterialDialog(fragment.requireContext())
			.show {
				title(R.string.permissionTitle)
				message(R.string.locationPermissionNeed)
				noAutoDismiss()
				cancelable(false)
				cancelOnTouchOutside(false)
				positiveButton(R.string._setting) {
					val intent = Intent()
					intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
					val uri = Uri.fromParts("package", fragment.activity?.packageName, null)
					intent.data = uri
					fragment.startActivity(intent)
					dismiss()
				}
				negativeButton(R.string._close) {
					locationProviderListener.onAccessDenied()
					dismiss()
				}
			}
	}

	private fun onPermissionDenied() {
		rxPermissions.shouldShowRequestPermissionRationale(
			fragment.activity,
			Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
		).subscribe({
			if (!it) {
				displayRationalDialog()
			} else {
				locationProviderListener.onAccessDenied()
			}
		}, {
			e(it.message)
		}).addTo(disposable)
	}

	private fun onPermissionLocationRequest() {
		rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
			.subscribeOn(AndroidSchedulers.mainThread())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe({ granted ->
				if (granted) {
					startLocationListening()
				} else {
					onPermissionDenied()
				}
			}, {
				e(it.message)
			}).addTo(disposable)
	}
}

interface LocationProviderListener {
	fun onAccessDenied() {}
	fun onLocationDetect(location: Location)
}