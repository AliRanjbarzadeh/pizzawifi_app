package ir.atriatech.pizzawifi.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.BuildConfig
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.databinding.CityBranchBottomsheetBinding
import ir.atriatech.pizzawifi.databinding.FragmentHomeBinding
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.home.*
import ir.atriatech.pizzawifi.ui.main.branch.BranchAdapter
import ir.atriatech.pizzawifi.ui.main.city.CityAdapter


class HomeFragment : BaseFragment() {

	lateinit var binding: FragmentHomeBinding
	private lateinit var mViewModel: HomeFragmentViewModel
	private lateinit var mCityBranchDialog: MaterialDialog
	private lateinit var cityBranchBottomsheetBinding: CityBranchBottomsheetBinding
	private var isShowCityBranchDialog = false

	private lateinit var rxPermissions: RxPermissions
	private lateinit var rxLocation: RxLocation
	private val locationRequest = LocationRequest()

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

		baseFragmentCallback?.changeMenuLock(true)

		//Location
		locationRequest.run {
			interval = 10000
			fastestInterval = 5000
			priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		}

		rxPermissions = RxPermissions(this)
		rxLocation = RxLocation(requireContext())

		mObserver()
		mBranchObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
		binding.viewModel = mViewModel

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			sharedElementReturnTransition =
				TransitionInflater.from(requireContext())
					.inflateTransition(android.R.transition.move)
		}

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		postponeEnterTransition()
		view.viewTreeObserver.addOnPreDrawListener {
			startPostponedEnterTransition()
			true
		}

		if (mViewModel.getData()) {
			setAdapter()
		} else {
			isShowCityBranchDialog = true
		}

		cityBranchBottomsheetBinding = DataBindingUtil.inflate(layoutInflater, R.layout.city_branch_bottomsheet, null, false)
		cityBranchBottomsheetBinding.viewModel = mViewModel
		cityBranchBottomsheetBinding.executePendingBindings()
		mCityBranchDialog = MaterialDialog(requireContext(), BottomSheet()).apply {
			customView(view = cityBranchBottomsheetBinding.root)
			cornerRadius(res = R.dimen._20mdp)

			if (AppObject.selectedBranchId == 0) {
				cancelable(false)
			}

			onDismiss {
				mViewModel.isShowBranch = false
				cityBranchBottomsheetBinding.btnNearestBranch.setIconResource(R.drawable.ic_baseline_location_searching_24)
				cityBranchBottomsheetBinding.btnNearestBranch.text = getString(R.string.nearestBranch)
				cityBranchBottomsheetBinding.btnNearestBranch.tag = null
				mViewModel.latLng = null
				mViewModel.locationSearch.set(false)
			}

			onShow {
				it.view.findViewById<LinearLayoutCompat>(R.id.mainView).setPadding(0, 0, 0, 0)
			}
		}

		cityBranchBottomsheetBinding.rvItems.layoutManager = LinearLayoutManager(requireContext())
		cityBranchBottomsheetBinding.rvItems.setHasFixedSize(true)

		cityBranchBottomsheetBinding.imgClose.setOnClickListener {
			if (AppObject.selectedBranchId == 0) {
				eToast(getString(R.string.pleaseSelectBranch))
				return@setOnClickListener
			}

			if (mViewModel.isShowBranch) {
				changeToCityAdapter()
				mViewModel.isShowBranch = false
				return@setOnClickListener
			}

			mCityBranchDialog.dismiss()
		}

		cityBranchBottomsheetBinding.btnNearestBranch.setOnClickListener {
			if (mViewModel.locationSearch.get() == true) {
				return@setOnClickListener
			}

			when (it.tag) {
				"clearFilter" -> {
					cityBranchBottomsheetBinding.btnNearestBranch.setIconResource(R.drawable.ic_baseline_location_searching_24)
					cityBranchBottomsheetBinding.btnNearestBranch.text = getString(R.string.nearestBranch)
					cityBranchBottomsheetBinding.btnNearestBranch.tag = null
					mViewModel.latLng = null
					mViewModel.locationSearch.set(false)
					mViewModel.getNearestBranches()
				}

				else -> {
					grantPermissions()
				}
			}
		}
	}

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
							e(e1.message.toString(), "LocationListener")
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
				d(it, "LocationListener")
				if (it != null) {
					mViewModel.latLng = LatLng(it.latitude, it.longitude)
					mViewModel.getNearestBranches()
				}
			}
	}

	override fun onResume() {
		super.onResume()
		if (IS_COMPETITION_DONE) {
			mViewModel.mItem.tournamentHome.already = 1
		}

		baseFragmentCallback?.changeBottomMenuColor(true)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		baseFragmentCallback?.changeBottomMenuColor(false)
	}

	override fun goToMenu() {
		val arg = extraArguments(mViewModel.mItem.categories, ARG_STRING_1)
		navTo(R.id.categoryFragment, arg)
	}

	private fun goToMaker() {
		navTo(R.id.makerStep1Fragment)
	}

	override fun goToMenuDetail(argument1: Any, key1: String, argument2: Any, key2: String) {
		val arg = extraArguments(argument1, key1, argument2, key2)
		navTo(R.id.productDetailFragment, arg = arg)
	}

	private fun checkUpdate() {
		when (mViewModel.mItem.homeExtra.update.updateType) {
			1 -> {
				MaterialDialog(requireContext())
					.apply {
						title(R.string.updateTitle)
						message(text = mViewModel.mItem.homeExtra.update.updateMessage)
						positiveButton(R.string.updateApp)
							.positiveButton {
								startAction(mViewModel.mItem.homeExtra.update.updateLink)
							}
						negativeButton(R.string.close)
							.negativeButton {
								showGiftPopup()
							}
						show()
					}
			}

			2 -> {
				MaterialDialog(requireContext())
					.apply {
						title(R.string.updateTitle)
						message(text = mViewModel.mItem.homeExtra.update.updateMessage)
						cancelable(false)
						cancelOnTouchOutside(false)
						positiveButton(R.string.updateApp)
							.positiveButton {
								startAction(mViewModel.mItem.homeExtra.update.updateLink)
							}
						negativeButton(R.string.close)
							.negativeButton {
								finishAffinity()
							}
						show()
					}
			}
			else -> showGiftPopup()
		}
	}

	override fun logOut(isSystem: Boolean) {
		super.logOut(isSystem)
		SHOULD_REFRESH_HOME = true
		mViewModel.getData()
	}

	private fun showGiftPopup() {
		if (mViewModel.mItem.homePopup != null) {
			context?.let {
				mDialog = MaterialDialog(it)
					.customView(R.layout.popup_gift, null, scrollable = false, noVerticalPadding = false, horizontalPadding = false, dialogWrapContent = false)

				val image =
					mDialog!!.getCustomView().findViewById<AppCompatImageView>(R.id.imgImage)

				val content =
					mDialog!!.getCustomView().findViewById<AppCompatTextView>(R.id.txtContent)

				if (mViewModel.mItem.homePopup!!.type == "content") {
					image.visibility = View.GONE
					content.visibility = View.VISIBLE
					content.text = mViewModel.mItem.homePopup!!.description ?: ""
				} else if (mViewModel.mItem.homePopup!!.type == "image" && mViewModel.mItem.homePopup!!.image != null) {
					image.visibility = View.VISIBLE
					content.visibility = View.GONE

					if (mViewModel.mItem.homePopup!!.objectType == "campaign") {
						image.setOnClickListener {
							//nav to campaign register
							if (mViewModel.mItem.homePopup!!.canGo) {
								val arg = extraArguments(mViewModel.mItem.homePopup!!.objectId!!, ARG_STRING_1)
								navTo(R.id.campaignRegisterFragment, arg)
							} else {
								eToast("شما قبلا در این کمپین شرکت کرده اید")
							}
						}
					}

					val url = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
						Uri.parse(BuildConfig.UPLOAD_URL_UNSAFE + mViewModel.mItem.homePopup!!.image!!)
					} else {
						Uri.parse(BuildConfig.UPLOAD_URL + mViewModel.mItem.homePopup!!.image!!)
					}
					mViewModel.imageLoader.load(
						url,
						image, false
					)
				}

				mDialog?.show {
					cancelable(true)
					cancelOnTouchOutside(true)
					getCustomView().findViewById<AppCompatImageView>(R.id.imgClose)
						?.setOnClickListener {
							dismiss()

						}
				}
			}
		} else {
			baseFragmentCallback?.handleNotification()
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			setCategories()
			setButtons()
//			setProducts()
//          setTournament()
			mViewModel.mAdapter = HomeAdapter(mViewModel.mItem.homeExtra.show_price)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else {
			if (SHOULD_REFRESH_HOME) {
				val adapterCounts = mViewModel.mAdapter!!.list.size
				mViewModel.mAdapter!!.list.clear()
				mViewModel.mAdapter!!.notifyItemRangeRemoved(0, adapterCounts)
				setCategories()
				setButtons()
				mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
			}

			mViewModel.mListItems.forEach { homeItem ->
				if (homeItem is HomeModel1) {
					homeItem.cityModel = loadFromSp(SELECTED_CITY_MODEL, CityModel())
					homeItem.cityModel.notifyChange()
				}
			}
		}

		binding.rvHome.layoutManager = LinearLayoutManager(requireContext())
		binding.rvHome.adapter = mViewModel.mAdapter
		binding.rvHome.setHasFixedSize(true)

		if (GO_TO_MENU) {
			GO_TO_MENU = false
			goToMenu()
		}

		if (GO_TO_MAKER) {
			GO_TO_MAKER = false
			goToMaker()
		}

		if (GO_SHOP_CART) {
			GO_SHOP_CART = false
			baseFragmentCallback?.goToShopCart()
		}

		if (AppObject.selectedBranchId > 0) {
			mViewModel.mItem.cities.filter { cityModel -> cityModel.id == AppObject.selectedCityId }.also { cities ->
				if (cities.size > 0) {
					cities.first().branches.filter { branch -> branch.id == AppObject.selectedBranchId }.also { branches ->
						if (branches.size > 0) {
							mViewModel.mItem.homeButtons.cityButtonText = branches.first().name
						}
					}
				}
			}
		}
		baseFragmentCallback?.changeBottomMenuColor(true)


		try {
			if (mViewModel.mItem.cities.size > 1) {
				mViewModel.bottomSheetTitle.set(getString(R.string.selectCity))
				mViewModel.mItem.cities.forEachIndexed { index, cityModel ->
					if (cityModel.id == AppObject.selectedCityId) {
						cityModel.status = 1
						mViewModel.lastPosition = index

						cityModel.branches.forEachIndexed { branchIndex, branch ->
							if (branch.id == AppObject.selectedBranchId) {
								branch.status = 1
								mViewModel.lastPositionBranch = branchIndex
							} else {
								branch.status = 0
							}
						}
					} else {
						cityModel.branches.forEachIndexed { branchIndex, branch ->
							branch.status = 0
						}
						cityModel.status = 0
					}
				}
				if (mViewModel.mCityAdapter == null) {
					mViewModel.mCityAdapter = CityAdapter(requireContext(), cityClicks)
					mViewModel.mCityAdapter!!.list.addAll(mViewModel.mItem.cities)
				}
				cityBranchBottomsheetBinding.rvItems.adapter = mViewModel.mCityAdapter
				mViewModel.mCityAdapter?.notifyItemRangeInserted(0, mViewModel.mItem.cities.size)
			} else {
				mViewModel.mItem.cities.first().branches.forEachIndexed { branchIndex, branch ->
					if (branch.id == AppObject.selectedBranchId) {
						branch.status = 1
						mViewModel.lastPositionBranch = branchIndex
					} else {
						branch.status = 0
					}
				}
				cityBranchBottomsheetBinding.btnNearestBranch.visibility = View.VISIBLE
				mViewModel.bottomSheetTitle.set(getString(R.string.selectBranch))
				if (mViewModel.mBranchAdapter == null) {
					mViewModel.mBranchAdapter = BranchAdapter(requireContext(), branchClicks)
					mViewModel.mBranchAdapter!!.list.addAll(mViewModel.mItem.cities.first().branches)
				}
				cityBranchBottomsheetBinding.rvItems.adapter = mViewModel.mBranchAdapter
				mViewModel.mCityAdapter?.notifyItemRangeInserted(0, mViewModel.mItem.cities.first().branches.size)
			}
		} catch (ex: Exception) {
			e(ex.message, "HomeFrag")
		}

		if (isShowCityBranchDialog && !SHOULD_REFRESH_HOME) {
			isShowCityBranchDialog = false
			mCityBranchDialog.show()
		}
		SHOULD_REFRESH_HOME = false
	}

	private val cityClicks = object : RecyclerViewTools {
		override fun <T> onItemClick(position: Int, view: View, item: T) {
			item as CityModel

			val shopCartCount = appDataBase.shopDao().countAllMainThread()

			if (AppObject.selectedCityId != 0 && AppObject.selectedCityId != item.id && shopCartCount > 0) {
				//Dismiss current dialog
				mDialog?.dismiss()

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
									mViewModel.mItem.cities[mViewModel.lastPosition].status = 0
									mViewModel.mCityAdapter?.notifyItemChanged(mViewModel.lastPosition)
								}

								item.status = 1
								mViewModel.lastPosition = position
								mViewModel.mCityAdapter?.notifyItemChanged(position)
								mViewModel.isShowBranch = true
								changeToBranchAdapter()
							}
						negativeButton(R.string._no)
						show()
					}
			} else {
				mViewModel.mItem.cities.forEachIndexed { index, cityModel ->
					if (cityModel.id == AppObject.selectedCityId) {
						cityModel.status = 0
						mViewModel.mCityAdapter?.notifyItemChanged(index)
					}
				}
//				if (mViewModel.lastPosition != -1) {
//					mViewModel.mItem.cities[mViewModel.lastPosition].status = 0
//					mViewModel.mCityAdapter?.notifyItemChanged(mViewModel.lastPosition)
//				}

				item.status = 1
				mViewModel.lastPosition = position
				mViewModel.mCityAdapter?.notifyItemChanged(position)
				mViewModel.isShowBranch = true
				changeToBranchAdapter()
			}
		}
	}

	private val branchClicks = object : RecyclerViewTools {
		override fun <T> onItemClick(position: Int, view: View, item: T) {
			item as Branch

			when (view.id) {
				R.id.imgCall -> {
					dialThisNumber(item.phone)
					return
				}

				R.id.imgLocation -> {
					val gmmIntentUri = Uri.parse("geo:0,0?q=" + item.lat.toString() + "," + item.lng.toString())
					val mapIntent = Intent(Intent.ACTION_VIEW)
					mapIntent.data = gmmIntentUri
					if (mapIntent.resolveActivity(context!!.packageManager) != null) {
						startActivity(mapIntent)
					}
					return
				}
			}

			val shopCartCount = appDataBase.shopDao().countAllMainThread()
			if (AppObject.selectedBranchId != 0 && AppObject.selectedBranchId != item.id && shopCartCount > 0) {
				//Dismiss current dialog
				mDialog?.dismiss()

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
									mViewModel.mItem.cities[mViewModel.lastPosition].branches[mViewModel.lastPositionBranch].status = 0
									mViewModel.mCityAdapter?.notifyItemChanged(mViewModel.lastPositionBranch)
								}

								if (item.id == AppObject.selectedBranchId) {
									mCityBranchDialog.dismiss()
									return@positiveButton
								}

								item.status = 1
								mViewModel.lastPositionBranch = position
								mViewModel.mCityAdapter?.notifyItemChanged(position)

								//Save city in session and AppObject
								saveToSp(SELECTED_CITY_MODEL, mViewModel.mItem.cities[mViewModel.lastPosition])
								AppObject.cityItem = mViewModel.mItem.cities[mViewModel.lastPosition]
								AppObject.selectedCityId = mViewModel.mItem.cities[mViewModel.lastPosition].id

								//Save branch in session and AppObject
								saveToSp(SELECTED_BRANCH_MODEL, item)
								AppObject.selectedBranchId = item.id
								AppObject.branchItem = item

								SHOULD_REFRESH_HOME = true
								mCityBranchDialog.cancelable(true)
								mCityBranchDialog.dismiss()
								mViewModel.getData()
							}
						negativeButton(R.string._no)
						show()
					}
			} else {
				mViewModel.mItem.cities.find { cityModel -> cityModel.id == AppObject.selectedCityId }?.also {
					it.branches.find { branch -> branch.id == AppObject.selectedBranchId }?.also {
						it.status = 0
					}
				}

				if (item.id == AppObject.selectedBranchId) {
					mCityBranchDialog.dismiss()
					return
				}

				item.status = 1
				mViewModel.lastPositionBranch = position
				mViewModel.mBranchAdapter?.notifyItemChanged(position)

				//Save city in session and AppObject
				saveToSp(SELECTED_CITY_MODEL, mViewModel.mItem.cities[mViewModel.lastPosition])
				AppObject.cityItem = mViewModel.mItem.cities[mViewModel.lastPosition]
				AppObject.selectedCityId = mViewModel.mItem.cities[mViewModel.lastPosition].id

				//Save branch in session and AppObject
				saveToSp(SELECTED_BRANCH_MODEL, item)
				AppObject.selectedBranchId = item.id
				AppObject.branchItem = item

				SHOULD_REFRESH_HOME = true
				mCityBranchDialog.dismiss()
				mViewModel.getData()
			}
		}
	}

	private fun changeToCityAdapter() {
		cityBranchBottomsheetBinding.btnNearestBranch.visibility = View.GONE
		cityBranchBottomsheetBinding.imgClose.setImageResource(R.drawable.ic_close_black_24dp)
		mViewModel.bottomSheetTitle.set(getString(R.string.selectCity))
		mViewModel.mItem.cities.forEachIndexed { index, cityModel ->
			if (cityModel.id == AppObject.selectedCityId) {
				cityModel.status = 1
				mViewModel.lastPosition = index

				cityModel.branches.forEachIndexed { branchIndex, branch ->
					if (branch.id == AppObject.selectedBranchId) {
						branch.status = 1
						mViewModel.lastPositionBranch = branchIndex
					} else {
						branch.status = 0
					}
				}
			} else {
				cityModel.status = 0
			}
			mViewModel.mCityAdapter?.notifyItemChanged(index)
		}
		if (mViewModel.mCityAdapter == null) {
			mViewModel.mCityAdapter = CityAdapter(requireContext(), cityClicks)
			mViewModel.mCityAdapter!!.list.addAll(mViewModel.mItem.cities)
		} else {
			mViewModel.mCityAdapter!!.list.clear()
			mViewModel.mCityAdapter!!.list.addAll(mViewModel.mItem.cities)
		}
		cityBranchBottomsheetBinding.rvItems.removeAllViews()
		cityBranchBottomsheetBinding.rvItems.adapter = mViewModel.mCityAdapter
	}

	private fun changeToBranchAdapter() {
		cityBranchBottomsheetBinding.btnNearestBranch.visibility = View.VISIBLE
		cityBranchBottomsheetBinding.imgClose.setImageResource(if (mViewModel.mItem.cities.size > 1) R.drawable.ic_arrow else R.drawable.ic_close_black_24dp)
		mViewModel.bottomSheetTitle.set(getString(R.string.selectBranch))
		mViewModel.mItem.cities[mViewModel.lastPosition].also {
			it.branches.forEachIndexed { index, branch ->
				if (branch.id == AppObject.selectedBranchId) {
					mViewModel.lastPositionBranch = index
					branch.status = 1
				} else {
					branch.status = 0
				}
				mViewModel.mBranchAdapter?.notifyItemChanged(index)
			}

			if (mViewModel.mBranchAdapter == null) {
				mViewModel.mBranchAdapter = BranchAdapter(requireContext(), branchClicks)
				mViewModel.mBranchAdapter!!.list.addAll(it.branches)
			} else {
				mViewModel.mBranchAdapter!!.list.clear()
				mViewModel.mBranchAdapter!!.list.addAll(it.branches)
			}

			cityBranchBottomsheetBinding.rvItems.removeAllViews()
			cityBranchBottomsheetBinding.rvItems.adapter = mViewModel.mBranchAdapter
		}
	}

	private fun showCityBranchDialog() {
		if (mViewModel.mItem.cities.size == 0) {
			toast(getString(R.string.noCityFound))
			return
		}
		if (mViewModel.mItem.cities.size > 1) {
			mViewModel.bottomSheetTitle.set(getString(R.string.selectCity))
			changeToCityAdapter()
		} else if (mViewModel.mItem.cities.size == 1) {
			mViewModel.bottomSheetTitle.set(getString(R.string.selectBranch))
			changeToBranchAdapter()
		}
		mCityBranchDialog.show()
	}

	private fun setCategories() {
		mViewModel.categories.addAll(mViewModel.mItem.categories)
	}

	private fun setButtons() {
		val homeModel1 = HomeModel1(object : RecyclerViewTools {
			override fun <T> onItemClick(position: Int, view: View, item: T) {
				item as HomeModel1
				when (view.id) {
					R.id.makePizza -> {
						if (item.homeButtons.makerActive == 0) {
							eToast(findString(R.string.makerDisabled))
							return
						}

						//Check branch selected or not
						if (AppObject.selectedBranchId == 0) {
							//Go to city fragment
							val arg = extraArguments(argument1 = MAKER_FRG, key1 = ARG_STRING_1)
							navTo(R.id.cityFragment2, arg)
						} else {
							//Go to maker
							navTo(R.id.makerStep1Fragment)
						}
					}
					R.id.restaurantMenu -> {
						if (item.homeButtons.menuActive == 0) {
							eToast(findString(R.string.menuDisabled))
							return
						}

						//Check branch selected or not
						if (AppObject.selectedBranchId == 0) {
							//Go to city fragment
							val arg = extraArguments(argument1 = MENU_FRG, key1 = ARG_STRING_1)
							navTo(R.id.cityFragment2, arg)
						} else {
							//Go to menu
							val arg = extraArguments(argument1 = mViewModel.mItem.categories, key1 = ARG_STRING_1)
							navTo(R.id.categoryFragment, arg)
						}
					}

					R.id.selectCity -> {
//						val arg = extraArguments(argument1 = HOME_FRG, key1 = ARG_STRING_1)
//						navTo(R.id.cityFragment2, arg = arg)
						showCityBranchDialog()
					}
				}
			}
		})
		homeModel1.homeButtons = mViewModel.mItem.homeButtons
		homeModel1.cityModel = loadFromSp(SELECTED_CITY_MODEL, CityModel())
		mViewModel.mListItems.add(homeModel1)
	}

	private fun setProducts() {
		for (i in 0 until mViewModel.mItem.blogCategories.size) {
			val homeCategory = mViewModel.mItem.blogCategories[i]
			val homeModel2 = HomeModel2(object : RecyclerViewTools {
				override fun <T> onItemClick(position: Int, view: View, item: T) {
					val blog = item as Blog
					val tag = HOME_FRG

					when (blog.type) {
						"link" -> {
							blog.link?.let {
								startAction(it)
							}
						}

						"internal_link" -> {
							blog.internalLink?.let {
								navToDeepLink(it.app.toUri())
							}
						}

						"instagram_campaign" -> {
							val arg = extraArguments(
								argument1 = blog.instagramCampaignId,
								key1 = ARG_STRING_1
							)
							navTo(R.id.campaignRegisterFragment, arg = arg)
						}

						"video" -> {
							val arg = extraArguments(
								argument1 = blog,
								key1 = ARG_STRING_1
							)
							navTo(R.id.blogVideoDetail, arg = arg)
						}

						"gallery" -> {
							val arg = extraArguments(
								argument1 = blog,
								key1 = ARG_STRING_1
							)
							navTo(R.id.blogGalleryDetail, arg = arg)
						}

						"slider", "webview" -> {
							val arg = extraArguments(
								argument1 = blog,
								key1 = ARG_STRING_1
							)
							navTo(R.id.blogDetail, arg = arg)
						}
					}
				}
			})
			homeModel2.id = homeCategory.id
			homeModel2.title = homeCategory.name
			homeModel2.color = homeCategory.color
			homeModel2.blogs.addAll(homeCategory.blogs)
			mViewModel.mListItems.add(homeModel2)
		}
	}

	private fun setTournament() {
		if (mViewModel.mItem.tournamentHome.isActive == 1) {
			val homeModel3 = HomeModel3(object : RecyclerViewTools {
				override fun <T> onItemClick(position: Int, view: View, item: T) {
					item as HomeModel3
					if (item.tournamentHome.isActive == 0) {
						return
					}
					if (item.tournamentHome.link.isNotEmpty()) {
						startAction(item.tournamentHome.link)
					} else {
						if (mViewModel.mItem.tournamentHome.already == 1) {
							eToast(mViewModel.mItem.tournamentHome.alreadyMsg)
							return
						}
						navTo(R.id.tournamentFragment)
					}
				}
			})
			homeModel3.tournamentHome = mViewModel.mItem.tournamentHome
			mViewModel.mListItems.add(homeModel3)
		}
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<HomeBase> {
			override fun onProgress(loading: Boolean) {
				baseFragmentCallback?.changeMenuLock(loading)
				setProgressView(binding.mainView, loading, type = 2)
			}

			override fun onSuccess(data: HomeBase) {
				//Clear old list
				mViewModel.mListItems.clear()

				mViewModel.mItem = data

				if (AppObject.userInfo.isLogin) {
					AppObject.userInfo.init(mViewModel.mItem.homeExtra.user)
					saveToSp(USER_INFO, AppObject.userInfo)
				}

				if (data.cities.size == 1) {
					saveToSp(SELECTED_CITY_MODEL, data.cities.first())
					AppObject.cityItem = data.cities.first()
					AppObject.selectedCityId = AppObject.cityItem!!.id
					mViewModel.lastPosition = 0
					if (data.cities.first().branches.size == 1) {
						saveToSp(SELECTED_BRANCH_MODEL, data.cities.first().branches.first())
						AppObject.branchItem = data.cities.first().branches.first()
						AppObject.selectedBranchId = AppObject.branchItem!!.id
						mViewModel.lastPositionBranch = 0

						//Set branch name to button if one branch exists
						mViewModel.mItem.homeButtons.cityButtonText = AppObject.branchItem!!.name
					} else {
						mViewModel.mItem.homeButtons.cityButtonText = findString(R.string.selectBranch)
					}
				} else {
					mViewModel.mItem.homeButtons.cityButtonText = if (AppObject.branchItem == null) {
						findString(R.string.selectCity)
					} else {
						AppObject.branchItem!!.name
					}
				}

				saveToSp("share", mViewModel.mItem.homeExtra.share)
				saveToSp("tax", mViewModel.mItem.homeExtra.tax)
				saveToSp("toll", mViewModel.mItem.homeExtra.toll)
				saveToSp("goAddress", mViewModel.mItem.homeExtra.goAddress)
				setAdapter()
				baseFragmentCallback?.setHomeRemoteModel(data)
				checkUpdate()

				if (AppObject.userInfo.isLogin)
					mViewModel.savePushToken()
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		}, 2)
	}

	private fun mBranchObserver() {
		baseObserver(
			this,
			mViewModel.mBranchObserver,
			object : RequestConnectionResult<MutableList<Branch>> {
				override fun onProgress(loading: Boolean) {
					mViewModel.locationSearch.set(true)
					baseFragmentCallback?.changeMenuLock(loading)
					setProgressView(cityBranchBottomsheetBinding.mainView, loading, type = 2)
					cityBranchBottomsheetBinding.rvItems.visibility = View.GONE
				}

				override fun onSuccess(data: MutableList<Branch>) {
					cityBranchBottomsheetBinding.rvItems.visibility = View.VISIBLE

					data.forEachIndexed { branchIndex, branch ->
						if (branch.id == AppObject.selectedBranchId) {
							branch.status = 1
							mViewModel.lastPositionBranch = branchIndex
						} else {
							branch.status = 0
						}
					}

					mViewModel.mBranchAdapter?.list?.clear()
					mViewModel.mBranchAdapter?.list?.addAll(data)
					mViewModel.mBranchAdapter?.notifyItemRangeChanged(0, data.size)

					mViewModel.locationSearch.set(false)
					if (mViewModel.latLng != null) {
						cityBranchBottomsheetBinding.btnNearestBranch.setIconResource(R.drawable.ic_baseline_my_location_24)
						cityBranchBottomsheetBinding.btnNearestBranch.text = getString(R.string.clearFilter)
						cityBranchBottomsheetBinding.btnNearestBranch.tag = "clearFilter"
					} else {
						cityBranchBottomsheetBinding.btnNearestBranch.setIconResource(R.drawable.ic_baseline_location_searching_24)
						cityBranchBottomsheetBinding.btnNearestBranch.text = getString(R.string.nearestBranch)
						cityBranchBottomsheetBinding.btnNearestBranch.tag = null
					}
				}

				override fun onFailed(errorMessage: String) {
					super.onFailed(errorMessage)
					cityBranchBottomsheetBinding.rvItems.visibility = View.VISIBLE
				}

				override fun onFailed(msg: Msg) {
					super.onFailed(msg)
					cityBranchBottomsheetBinding.rvItems.visibility = View.VISIBLE
				}
			}
		)
	}
}