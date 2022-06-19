package ir.atriatech.pizzawifi.ui.main.city

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.orhanobut.logger.Logger
import com.patloew.rxlocation.RxLocation
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tbruyelle.rxpermissions2.RxPermissions
import ir.atriatech.core.ARG_PARCELABLE_1
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.ARG_STRING_3
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.databinding.FragmentCityBinding
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.Product


class CityFragment : BaseFragment(), OnRefreshListener, RecyclerViewTools {
	lateinit var binding: FragmentCityBinding
	private lateinit var mViewModel: CityFragmentViewModel

	//RxPermissions & RxLocation
	private lateinit var rxPermissions: RxPermissions
	private lateinit var rxLocation: RxLocation
	private val locationRequest = LocationRequest()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		component.inject(this)
		//Location
		locationRequest.run {
			interval = 10000
			fastestInterval = 5000
			priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		}

		rxPermissions = RxPermissions(this)
		rxLocation = RxLocation(requireContext())

		mViewModel = ViewModelProvider(this).get(CityFragmentViewModel::class.java)
		try {
			arguments?.getString(ARG_STRING_1)?.let {
				mViewModel.tag = it
			}

			arguments?.getParcelable<Product>(ARG_STRING_3)?.let {
				mViewModel.mItem = it
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
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_city, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (mViewModel.getData()) {
			setAdapter()
		}

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)
	}

	override fun onDestroy() {
		super.onDestroy()

		mViewModel.permissionDisposable?.dispose()
		mViewModel.rxLocationDisposable?.dispose()
	}


	/**
	 * Get permissions
	 */
	@SuppressLint("MissingPermission")
	private fun grantPermissions() {
		mViewModel.permissionDisposable?.dispose()
		mViewModel.permissionDisposable =
			rxPermissions.request(
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			)
				.subscribe {
					if (it) {
						startLocationListening()
					}
				}
	}

	/**
	 * Start getting location
	 */
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

		val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
		val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
		task.addOnSuccessListener(locationSettingSuccess)
		task.addOnFailureListener(locationSettingFailure)
	}

	private val locationSettingSuccess = OnSuccessListener<LocationSettingsResponse> {
		makeFusedLocation()
	}

	private val locationSettingFailure = OnFailureListener { e ->
		when (e) {
			is ApiException -> {
				when (e.statusCode) {
					LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
						try {
							val resolvable = e as ResolvableApiException
							resolvable.startResolutionForResult(activity, mViewModel.GPS_RESULT)
						} catch (e1: Exception) {
							e1.printStackTrace()
							Logger.e(e1.message.toString())
							//TODO: toast gps problem
						}
					}

					else -> {
						//TODO: toast gps problem
					}
				}
			}

			else -> {
				//TODO: toast gps problem
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			mViewModel.GPS_RESULT -> {
				when (resultCode) {
					Activity.RESULT_OK -> {
						makeFusedLocation()
					}

					else -> mViewModel.locationSearch.set(false)
				}
			}
		}
	}

	@SuppressLint("MissingPermission")
	fun makeFusedLocation() {
		mViewModel.rxLocationDisposable?.dispose()
		mViewModel.rxLocationDisposable = rxLocation.location()
			.updates(locationRequest)
			.doOnSubscribe {
				mViewModel.locationSearch.set(true)
				mViewModel.viewModelLoading.set(true)
			}
			.subscribe {
				if (it != null) {
					mViewModel.latLng = LatLng(it.latitude, it.longitude)
					mViewModel.getNearestBranches()
				}
			}
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as CityModel
		val shopCartCount = appDataBase.shopDao().countAllMainThread()

		//Dismiss current dialog
		mDialog?.dismiss()

		//Show change city dialog if shop cart not empty and other branch selected
		if (AppObject.selectedCityId != 0 && AppObject.selectedCityId != item.id && shopCartCount > 0) {
			mDialog = MaterialDialog(requireContext())
				.apply {
					title(
						text = String.format(
							getString(R.string.sureSelectBranch),
							item.name
						)
					)
					message(
						text = String.format(
							getString(R.string.someOrderInBasketCity),
							AppObject.cityItem?.name
						)
					)
					positiveButton(R.string._yes)
						.positiveButton {
							//Delete all items in shop cart
							appDataBase.shopDao().deleteAll()

							if (mViewModel.lastPosition != -1) {
								mViewModel.mListItems[mViewModel.lastPosition].isSelected = false
								mViewModel.mAdapter?.notifyItemChanged(mViewModel.lastPosition)
							}

							item.isSelected = true
							mViewModel.lastPosition = position
							mViewModel.mAdapter?.notifyItemChanged(position)

							//Go to branches or back to previous fragment
							goToDestination(item)
						}
					negativeButton(R.string._no)
					show()
				}
		} else {
			if (mViewModel.lastPosition != -1) {
				mViewModel.mListItems[mViewModel.lastPosition].isSelected = false
				mViewModel.mAdapter?.notifyItemChanged(mViewModel.lastPosition)
			}

			item.isSelected = true
			mViewModel.lastPosition = position
			mViewModel.mAdapter?.notifyItemChanged(position)

			//Go to branches or back to previous fragment
			goToDestination(item)
		}
	}

	fun goToDestination(cityModel: CityModel) {
		when {
			cityModel.branches.size == 1 -> {
				//Save city in session and AppObject
				saveToSp(SELECTED_CITY_MODEL, cityModel)
				AppObject.cityItem = cityModel
				AppObject.selectedCityId = cityModel.id

				//Save branch in session and AppObject
				val branch = cityModel.branches.first()
				saveToSp(SELECTED_BRANCH_MODEL, branch)

				//Make home refresh after select branch
				if (AppObject.selectedBranchId != branch.id) {
					SHOULD_REFRESH_HOME = true
				}

				AppObject.branchItem = branch
				AppObject.selectedBranchId = branch.id

				when (mViewModel.tag) {
					MENU_FRG -> GO_TO_MENU = true

					MAKER_FRG -> GO_TO_MAKER = true
				}

				//Back to previous fragment
				back()
			}

			cityModel.branches.size > 0 -> {
				val arg = extraArguments(argument1 = cityModel, key1 = ARG_PARCELABLE_1, argument2 = mViewModel.tag, key2 = ARG_STRING_1)
				navTo(R.id.branchFragment2, arg)
			}

			else -> {
				mDialog?.dismiss()
				mDialog = MaterialDialog(requireContext())
					.apply {
						title(
							text = findString(R.string._error_title)
						)
						message(
							text = findString(R.string.cityHasNoBranch)
						)
						show()
					}
			}
		}
	}

	override fun onRefresh(refreshLayout: RefreshLayout) {
		mViewModel.isRefreshing = true
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		mViewModel.mAdapter?.notifyDataSetChanged()
		mViewModel.getData()
	}

	fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = CityAdapter(requireContext(), this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		try {
			binding.rvBranch.removeItemDecorationAt(0)
		} catch (ex: Exception) {
		}

		binding.rvBranch.setHasFixedSize(true)
		binding.rvBranch.layoutManager = LinearLayoutManager(requireContext())
		binding.rvBranch.addItemDecoration(
			MarginItemDecoration(
				dp2px(2),
				marginPosition = MarginItemDecoration.TOP
			)
		)

		if (AppObject.selectedCityId > 0) {
			if (mViewModel.mListItems.isNotEmpty()) {
				for (i in 0 until mViewModel.mListItems.size) {
					if (mViewModel.mListItems[i].id == AppObject.selectedCityId) {
						if (mViewModel.lastPosition > -1 && mViewModel.lastPosition != i) {
							mViewModel.mListItems[mViewModel.lastPosition].isSelected = false
							mViewModel.mAdapter!!.list[mViewModel.lastPosition].isSelected = false
						}

						mViewModel.mListItems[i].isSelected = true
						mViewModel.mAdapter!!.list[i].isSelected = true
						mViewModel.lastPosition = i
						break
					}
				}
			}
		}

		binding.rvBranch.adapter = mViewModel.mAdapter
	}

	private fun mObserver() {
		baseObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<CityModel>> {
				override fun onProgress(loading: Boolean) {
					if (!mViewModel.isRefreshing) {
						setProgressView(binding.mainView, loading, 2)
					}
				}

				override fun onSuccess(data: MutableList<CityModel>) {
					mViewModel.viewModelLoading.set(false)
					mViewModel.mListItems = data
					mViewModel.lastPosition = -1
					binding.refreshLayout.finishRefresh()
					mViewModel.isEmptyView.set(data.size == 0)
					mViewModel.isShowContent.set(data.size != 0)

//                    if (mViewModel.latLng != null) {
//                        actionButton.setIconResource(R.drawable.ic_close_white_24dp)
//                        actionButton.text = getString(R.string.clearFilter)
//                        actionButton.tag = "clearFilter"
//                    }
					setAdapter()
					if (mViewModel.locationSearch.get() && mViewModel.mListItems.isNotEmpty() && mViewModel.mListItems.size == 1) {
						mViewModel.locationSearch.set(false)
						when (mViewModel.tag) {
							MENU_FRG -> {
								val arg = extraArguments(
									argument1 = mViewModel.tag,
									key1 = ARG_STRING_1,
									argument2 = mViewModel.mListItems[0],
									key2 = ARG_STRING_2
								)

								navTo(R.id.branchFragment2, arg = arg)
							}

							ONLY_SELECT_CITY -> back()

							else -> {
								val arg = extraArguments(
									argument1 = mViewModel.tag,
									key1 = ARG_STRING_1,
									argument2 = mViewModel.mListItems[0],
									key2 = ARG_STRING_2,
									argument3 = mViewModel.mItem,
									key3 = ARG_STRING_3
								)

								navTo(R.id.branchFragment2, arg = arg)
							}
						}
					} else {
						mViewModel.locationSearch.set(false)
					}
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.viewModelLoading.set(false)
					mViewModel.getData()
				}
			},
			4
		)
	}
}