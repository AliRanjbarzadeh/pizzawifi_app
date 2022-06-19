package ir.atriatech.pizzawifi.ui.main.orders.detail

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.reflect.TypeToken
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findDrawable
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MyItemDecoration
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.databinding.FragmentOrderDetailBinding
import ir.atriatech.pizzawifi.entities.orders.CourierInfo
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.OrderItem
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import java.lang.reflect.Type


class OrderDetailFragment : BaseFragment() {
	lateinit var binding: FragmentOrderDetailBinding
	private lateinit var mViewModel: OrderDetailFragmentViewModel
	private var lastMarker: Marker? = null

	//region Map
	private lateinit var mapView: SupportMapFragment
	private var mGoogleMap: GoogleMap? = null

	//endregion
	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(OrderDetailFragmentViewModel::class.java)

		try {
			arguments?.getInt(ARG_STRING_1)?.let { mViewModel.mID = it }
//            arguments?.getParcelable<Order>(ARG_STRING_1)?.let { mViewModel.mItem.set(it) }
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
			DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)

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

		binding.imgMap.setOnTouchListener { _, event ->
			when (event.getAction()) {
				MotionEvent.ACTION_DOWN -> {
					binding.mainNested.requestDisallowInterceptTouchEvent(true)
					false
				}
				MotionEvent.ACTION_UP -> {
					binding.mainNested.requestDisallowInterceptTouchEvent(false)
					true
				}
				MotionEvent.ACTION_MOVE -> {
					binding.mainNested.requestDisallowInterceptTouchEvent(true)
					false
				}

				else -> {
					true
				}
			}
		}


		binding.btnReorder.setOnClickListener {
			if (!mViewModel.mItem.get()!!.isReorder) {
				eToast(findString(R.string.reorderError))
				return@setOnClickListener
			}

			if (mDialog == null || mDialog?.isShowing == false) {
				mDialog = MaterialDialog(requireContext())
					.apply {
						title(R.string.reorder)
						message(R.string.reorderDescription)
						positiveButton(R.string._yes)
							.positiveButton {
								appDataBase.shopDao().deleteAll()
								reorder()
							}
						negativeButton(R.string._no)
						show()
					}
			}
		}

		binding.btnSurvey.setOnClickListener {
			if (mViewModel.mItem.get()?.survey == 1) {
				eToast(findString(R.string.alreadySurvey))
				return@setOnClickListener
			}
			if (mViewModel.mItem.get()?.orderStatus != 3) {
				eToast(findString(R.string.onlyDeliveredOrdersSurvey))
				return@setOnClickListener
			}
			val arg = extraArguments(argument1 = mViewModel.mItem.get()!!, key1 = ARG_STRING_1)
			navTo(R.id.surveyFragment, arg = arg)
		}
	}

	override fun onResume() {
		super.onResume()
		if (GO_TO_ORDERS) {
			removeUntil(R.id.ordersFragment)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		if (mViewModel.mItem.get()?.orderStatus in arrayOf(
				0,
				1,
				2
			) && mViewModel.mItem.get()?.isTimer == true
		) {
			binding.mTimer.stop()
			mViewModel.mItem.get()?.waitTime =
				binding.mTimer.remainTime + System.currentTimeMillis()
		}
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		when (key) {
			"orders" -> {
				if (additional.isNotEmpty()) {
					try {
						val msg = gson.fromJson<Msg>(additional, Msg::class.java)
						if (msg.id != mViewModel.mID) {
							val arg = extraArguments(msg.id, ARG_STRING_1)
							navTo(R.id.orderDetailFragment, arg = arg)
						} else {
							mViewModel.isRefresh = true
							mViewModel.getData()
						}
					} catch (ex: Exception) {
					} finally {
						deleteFromSp(NOTIFICATION_KEY)
						deleteFromSp(NOTIFICATION_ADDITIONAL)
					}
				}
			}
			"peyk" -> {
				d("peyk location chaneg --> ${additional}", "peyk location")
				try {
					if (additional.isNotEmpty()) {
						val courierInfo =
							gson.fromJson<CourierInfo>(additional, CourierInfo::class.java)
						val latLng = LatLng(courierInfo.lat, courierInfo.lng)
						lastMarker?.position = latLng
						mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
					}
				} catch (ex: Exception) {
				} finally {
					deleteFromSp(NOTIFICATION_KEY)
					deleteFromSp(NOTIFICATION_ADDITIONAL)
				}
			}

		}
	}

	fun showMap() {
		try {
			mapView = SupportMapFragment.newInstance()
			childFragmentManager.beginTransaction()
				.replace(R.id.frameCourierMap, mapView)
				.commit()
			mapView.getMapAsync(onMapReady)
		} catch (ex: Exception) {
			e(ex.message, "MapFragment")
		}
	}

	/**
	 * On map ready
	 */
	@SuppressLint("MissingPermission")
	private val onMapReady = OnMapReadyCallback { googleMap ->
		mGoogleMap = googleMap

		val target = LatLng(
			mViewModel.mItem.get()!!.courierInfo.lat,
			mViewModel.mItem.get()!!.courierInfo.lng
		)

		val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_motor)
		lastMarker = mGoogleMap?.addMarker(
			MarkerOptions().apply {
				title(mViewModel.mItem.get()!!.courierInfo.name)
				position(target)
				icon(BitmapDescriptorFactory.fromBitmap(bitmap))
				draggable(false)
			}
		)
		lastMarker?.showInfoWindow()
		mGoogleMap?.setOnMapClickListener { lastMarker?.showInfoWindow() }

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
	}

	private fun reorder() {
		val items = mutableListOf<ShopCartItem>()
		mViewModel.mListItems.forEach { orderItem ->
			val shopCartItem = ShopCartItem()
			shopCartItem.apply {
				productID = orderItem.id
				name = orderItem.name
				price = orderItem.updatePrice
				type = orderItem.type
				max_choice = orderItem.max_choice
				//todo:  add branch id and name to the items ================
				if (mViewModel.mItem.get()?.branch != null) {
					branchId = mViewModel.mItem.get()?.branch!!.id
					branchName = mViewModel.mItem.get()?.branch!!.name
					branchAddress = mViewModel.mItem.get()?.branch!!.address
				}
				discount_percent = orderItem.discount_percent
				discount = orderItem.updateDiscount
				materials = gson.toJson(orderItem.materials)
				qty = orderItem.qty
				if (orderItem.type == 1) {
					pizza = 1
				} else {
					image = orderItem.image
					pizza = 0
				}
			}
			items.add(shopCartItem)
		}
		appDataBase.shopDao().saveItem(*items.toTypedArray())

		//region Set city and branch from this order in session
		//Save city in session and AppObject
		saveToSp(SELECTED_CITY_MODEL, mViewModel.mItem.get()!!.city)
		AppObject.selectedCityId = mViewModel.mItem.get()!!.city.id
		AppObject.cityItem = mViewModel.mItem.get()!!.city

		//Save branch in session and AppObject
		saveToSp(SELECTED_BRANCH_MODEL, mViewModel.mItem.get()!!.city.branches.first())

		//Make home refresh after select branch
		if (AppObject.selectedBranchId != mViewModel.mItem.get()!!.city.branches.first().id) {
			SHOULD_REFRESH_HOME = true
		}
		AppObject.selectedBranchId = mViewModel.mItem.get()!!.city.branches.first().id
		AppObject.branchItem = mViewModel.mItem.get()!!.city.branches.first()
		//endregion

		GO_SHOP_CART = true
		removeUntil(R.id.ordersFragment)
	}

	private fun setAdapter() {
		//set order status text and color
		binding.txtOrderStatus.text = mViewModel.mItem.get()?.statusText
		binding.txtOrderStatus.background = when (mViewModel.mItem.get()?.orderStatus) {
			0 -> findDrawable(R.drawable.bg_status0)
			1 -> findDrawable(R.drawable.bg_status1)
			2 -> findDrawable(R.drawable.bg_status2)
			3 -> findDrawable(R.drawable.bg_status3)
			4 -> findDrawable(R.drawable.bg_status4)
			else -> findDrawable(R.drawable.bg_status0)
		}



		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = OrderItemsAdapter()
			try {
				val listType: Type = object : TypeToken<MutableList<OrderItem>>() {}.type
				mViewModel.mListItems.addAll(
					gson.fromJson(
						mViewModel.mItem.get()!!.orderItems,
						listType
					)
				)
				mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
			} catch (ex: Exception) {
			}
			if (mViewModel.mItem.get()?.isTimer == true) {
				if (mViewModel.mItem.get()?.orderStatus in arrayOf(0, 1, 2)) {
					if (mViewModel.mItem.get()!!.waitTime * 1000 - System.currentTimeMillis() > 0) {
						binding.mTimer.start(mViewModel.mItem.get()!!.waitTime * 1000 - System.currentTimeMillis())
					} else {
						binding.mTimer.start(3000000)
					}
				} else {
					binding.mTimer.updateShow(mViewModel.mItem.get()!!.waitTime * 1000)
				}
			}
		} else {
			if (mViewModel.mItem.get()?.isTimer == true) {
				if (mViewModel.mItem.get()?.orderStatus in arrayOf(0, 1, 2)) {
					binding.mTimer.start(mViewModel.mItem.get()!!.waitTime - System.currentTimeMillis())
				} else {
					binding.mTimer.updateShow(mViewModel.mItem.get()!!.waitTime * 1000)
				}
			}
		}
		mViewModel.isRefresh = false

		try {
			binding.rvOrderItems.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}

		binding.rvOrderItems.setHasFixedSize(false)
		binding.rvOrderItems.isNestedScrollingEnabled = false
		binding.rvOrderItems.layoutManager = LinearLayoutManager(requireContext())
		binding.rvOrderItems.adapter = mViewModel.mAdapter
		binding.rvOrderItems.addItemDecoration(
			MyItemDecoration(
				findColor(R.color.colorAA53),
				dp2px(1)
			)
		)
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Order> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Order) {
				mViewModel.mItem.set(data)
				if (data.showCourier == 1) {
					showMap()
				}
				setAdapter()
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		}, 4)
	}
}