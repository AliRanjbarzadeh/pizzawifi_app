package ir.atriatech.pizzawifi.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.databinding.FragmentProfileBinding
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.MyMenuItem
import ir.atriatech.pizzawifi.ui.MyMenuAdapter


class ProfileFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentProfileBinding
	private lateinit var mViewModel: ProfileFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

		mViewModel.cityModel = loadFromSp(SELECTED_CITY_MODEL, CityModel())
		binding.lifecycleOwner = this
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.imgUpload.setOnClickListener { pickImage() }
		setAdapter()
		loadImage()

		if (mViewModel.userInfo.name.isEmpty())
			mViewModel.userInfo = AppObject.userInfo

		binding.btnIncreaseWallet.setOnClickListener {
			AppObject.isShowKeyboard = true
			navTo(R.id.walletFragment)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		pickImageOnActivityResult(requestCode, data, object : PickImageCallback {
			override fun onSuccess(uri: Uri) {
				mViewModel.uploadImage(uri)
			}

			override fun onFail(error: Exception) {
				eToast(findString(R.string.wrongImageValue))
			}
		})
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		deleteFromSp(NOTIFICATION_KEY)
		deleteFromSp(NOTIFICATION_ADDITIONAL)
		when (key) {
			"wallet" -> {
				navTo(R.id.walletFragment)
			}

			"support" -> {
				navTo(R.id.supportFragment)
			}

			"message" -> {
				navTo(R.id.messagesFragment)
			}
		}
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		when (position) {
			0 -> {//edit info
				navTo(R.id.editInfoFragment)
			}

			1 -> {//change number
				navTo(R.id.changeMobileStep1Fragment)
			}

			2 -> {//support
				navTo(R.id.supportFragment)
			}

			3 -> {//addresses
				navTo(R.id.addressFragment)
			}

			4 -> {//My pizza
				navTo(R.id.pizzaFragment)
			}

			5 -> {//wallet
				navTo(R.id.walletFragment)
			}

			6 -> {//messages
				navTo(R.id.messagesFragment)
			}

			7 -> {//logout
				if (mDialog == null || mDialog?.isShowing == false) {
					mDialog = MaterialDialog(requireContext())
						.apply {
							title(R.string.logoutTitle)
							message(R.string.logoutDescription)
							positiveButton(R.string.logoutTitle)
								.positiveButton {
									val bundle = Bundle()
									bundle.putString("name", AppObject.userInfo.name)
									bundle.putString("mobile", AppObject.userInfo.mobile)
									baseFragmentCallback?.logFirebase("user_logout", bundle)
									mViewModel.logout()
									appDataBase.shopDao().deleteAll()
									baseFragmentCallback?.logout()
								}
							negativeButton(R.string._no)
							show()
						}
				}
			}
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = MyMenuAdapter(this)

			val titles = resources.getStringArray(R.array.profileItems)
			val icons = resources.obtainTypedArray(R.array.profileIcons)

			for (i in titles.indices) {
				mViewModel.mAdapter!!.list.add(
					MyMenuItem(
						title = titles[i],
						image = icons.getResourceId(i, -1)
					)
				)
			}
			icons.recycle()
		}

		try {
			binding.rvProfile.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}

		binding.rvProfile.setHasFixedSize(true)
		binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
		binding.rvProfile.addItemDecoration(
			MarginItemDecoration(
				mHeight = dp2px(1),
				marginPosition = 1,
				isShowOnFirstItem = true
			)
		)
		binding.rvProfile.adapter = mViewModel.mAdapter
		handleNotification()
	}

	private fun loadImage() {
		if (mViewModel.userInfo.image.isNotEmpty()) {
			imageLoader.load(
				mViewModel.userInfo.image.getImageUri(isCustomSize = true, size = "2x"),
				binding.imgProfile
			)
		}
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.loaderContainer, loading)
			}

			override fun onSuccess(data: Msg) {
				AppObject.userInfo.image = data.image
				mViewModel.userInfo.image = data.image
				saveToSp(USER_INFO, AppObject.userInfo)
				sToast(data.msg)
				loadImage()
			}
		}, 1)
	}
}