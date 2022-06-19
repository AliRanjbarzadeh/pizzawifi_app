package ir.atriatech.pizzawifi.ui.main.more.contact

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentContactUsBinding
import ir.atriatech.pizzawifi.entities.more.contactus.ContactItem
import ir.atriatech.pizzawifi.entities.more.contactus.ContactUs

class ContactUsFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentContactUsBinding
	private lateinit var mViewModel: ContactUsFragmentViewModel

	//region Map
	private lateinit var mapView: SupportMapFragment
	private var mGoogleMap: GoogleMap? = null
	//endregion

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ContactUsFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false)
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { back() }
		binding.viewModel = mViewModel
		return binding.root
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
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
		if (mViewModel.getData()) {
			setData()
		}
	}

	private fun setData() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mListItem.clear()

			val socialsType = mutableListOf<String>("telegram", "facebook", "twitter", "instagram")
			val hasSocial = mViewModel.mItem.contactItems.filter { contactItem -> contactItem.type in socialsType }
			if (hasSocial.size > 0) {
				mViewModel.mListItem.add(ContactItem(title = findString(R.string.findUsInSocials), isHasTitle = true))
			}
			mViewModel.mItem.contactItems.forEach { contactItem ->
				when (contactItem.type) {
					"telegram" -> {
						contactItem.image = R.drawable.ic_telegram
						mViewModel.mListItem.add(contactItem)
					}
					"whatsapp" -> {
						contactItem.image = R.drawable.ic_whatsapp
						mViewModel.mListItem.add(contactItem)
					}
					"instagram" -> {
						contactItem.image = R.drawable.ic_instagram
						mViewModel.mListItem.add(contactItem)
					}
					"facebook" -> {
						contactItem.image = R.drawable.ic_facebook
						mViewModel.mListItem.add(contactItem)
					}
					"twitter" -> {
						contactItem.image = R.drawable.ic_twitter
						mViewModel.mListItem.add(contactItem)
					}
					"youtube" -> {
						contactItem.image = R.drawable.ic_youtube
						mViewModel.mListItem.add(contactItem)
					}
				}
			}

			mViewModel.mListItem.add(ContactItem(title = findString(R.string.contactUsInfo), isHasTitle = true))
			mViewModel.mItem.contactItems.forEach { contactItem ->
				when (contactItem.type) {
					"address" -> {
						contactItem.image = R.drawable.ic_location_more
						mViewModel.mListItem.add(contactItem)
					}
					"tel" -> {
						contactItem.image = R.drawable.ic_phone
						mViewModel.mListItem.add(contactItem)
					}
					"email" -> {
						contactItem.image = R.drawable.ic_email
						mViewModel.mListItem.add(contactItem)
					}
				}
			}
		}

		if (mViewModel.mItem.myLocation.showMap == 1) {
			showMap()
		} else {
			binding.cardMap.visibility = View.GONE
		}
		setAdapter()
	}

	private fun showMap() {
		try {
			mapView = SupportMapFragment.newInstance()
			childFragmentManager.beginTransaction()
				.replace(R.id.cardMap, mapView)
				.commit()
			mapView.getMapAsync(onMapReady)
		} catch (ex: Exception) {
			e(ex.message, "MapFragment")
		}
	}

	//Map ready
	private val onMapReady = OnMapReadyCallback { googleMap ->
		mGoogleMap = googleMap
		val target = LatLng(mViewModel.mItem.myLocation.lat, mViewModel.mItem.myLocation.lng)

		val cameraPosition =
			CameraPosition.Builder()
				.zoom(15f)
				.target(target)
				.build()

		mGoogleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

		val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_marker)
		mGoogleMap?.addMarker(
			MarkerOptions().apply {
				title(mViewModel.mItem.myLocation.title)
				position(target)
				icon(BitmapDescriptorFactory.fromBitmap(bitmap))
				draggable(false)
			}
		)

		mGoogleMap?.let {
			val uiSettings = it.uiSettings
			uiSettings.isMyLocationButtonEnabled = false
			uiSettings.isCompassEnabled = true
			uiSettings.isZoomControlsEnabled = true
			uiSettings.isIndoorLevelPickerEnabled = false
			uiSettings.isRotateGesturesEnabled = false
			uiSettings.isTiltGesturesEnabled = false
		}
//        if (RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            mGoogleMap?.isMyLocationEnabled = true
//        }
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as ContactItem
		when (item.type) {
			"address" -> shareText(item.value)
			"tel" -> dialThisNumber(item.value)
			"email" -> emailTo(
				item.value,
				subject = "لاویا - اپلیکیشن اندروید",
				suffix = "ایمیل ارسال شده از طرف ${AppObject.userInfo.name}\nاپلیکیشن لاویا"
			)
			"facebook", "twitter", "instagram", "whatsapp", "youtube", "telegram" -> startAction(
				item.link
			)
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = ContactUsAdapter(this)
			if (mViewModel.mListItem.isNotEmpty()) {
				mViewModel.mAdapter!!.list.clear()
				mViewModel.mAdapter!!.list.addAll(mViewModel.mListItem)
			}
		}

		binding.rvContactUs.setHasFixedSize(false)
		binding.rvContactUs.isNestedScrollingEnabled = false
		binding.rvContactUs.layoutManager = LinearLayoutManager(requireContext())
		binding.rvContactUs.addItemDecoration(
			MarginItemDecoration(
				mHeight = dp2px(4),
				marginPosition = MarginItemDecoration.BOTTOM
			)
		)
		binding.rvContactUs.adapter = mViewModel.mAdapter
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<ContactUs> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainCoordinator, loading, type = 2)
			}

			override fun onSuccess(data: ContactUs) {
				mViewModel.mItem = data
				setData()
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		}, 4)
	}
}