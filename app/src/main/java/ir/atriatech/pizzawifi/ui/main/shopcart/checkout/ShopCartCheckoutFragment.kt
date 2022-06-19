package ir.atriatech.pizzawifi.ui.main.shopcart.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.databinding.FragmentShopCartCheckoutBinding
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.ProductMaterial
import ir.atriatech.pizzawifi.entities.UserInfo
import ir.atriatech.pizzawifi.entities.orders.OrderItemJson
import ir.atriatech.pizzawifi.entities.orders.OrderJson
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.shopcart.BreadJson
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartTools
import java.lang.reflect.Type

class ShopCartCheckoutFragment : BaseFragment() {
	lateinit var binding: FragmentShopCartCheckoutBinding
	private lateinit var mViewModel: ShopCartCheckoutFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ShopCartCheckoutFragmentViewModel::class.java)
		try {
			arguments?.getParcelable<Address>(ARG_STRING_1)?.let {
				mViewModel.address = it
			}

		} catch (e: Exception) {
		}
		mViewModel.branch = AppObject.branchItem
		mToolsObserver()
		mDiscountObserver()
		mAddOrderObserver()
		mCheckPaymentObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_shop_cart_checkout,
			container,
			false
		)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		//region Hide keyboard
		hideKeyboard(binding.mainView)
		hideKeyboard(binding.llMain)
		hideKeyboard(binding.card1)
		hideKeyboard(binding.card2)
		hideKeyboard(binding.card3)
		hideKeyboard(binding.card4)
//        hideKeyboard(binding.card5)
		//endregion

		mViewModel.mListItems = appDataBase.shopDao().getAll()
		if (mViewModel.mListItems.isEmpty()) {
			removeUntil(R.id.shopCartFragment)
		} else {
			calculatePrice()
			mViewModel.getTools()
		}

		binding.btnChangeAddress.setOnClickListener { back() }

		binding.btnWallet.setOnCheckedChangeListener { _, isChecked: Boolean ->
			mViewModel.isWallet = if (isChecked) {
				1
			} else {
				0
			}
			calculatePrice()
		}

		binding.btnDiscount.setOnClickListener {
			if (mViewModel.checkoutPrices.discountPercent > 0) {
				mViewModel.checkoutPrices.discountCode = ""
				calculatePrice()
			} else {
				mViewModel.checkDiscount()
			}
		}

		binding.btnAdd.setOnClickListener {
			if (mViewModel.shopCartTools.now == 0 && mViewModel.shopCartTools.reserve == 0) {
				eToast(findString(R.string.outOfTimeOrderError))
				return@setOnClickListener
			}
			makeOrderJson()
			mViewModel.addOrder()
		}

		binding.payOnline.setOnClickListener {
			if (mViewModel.checkoutPrices.totalToPay == 0)
				return@setOnClickListener

			mViewModel.checkoutPrices.paymentType = 1
			binding.payOnline.strokeColor = findColor(R.color.colorPrimary)
			binding.payOnline.invalidate()
			binding.payOffline.strokeColor = findColor(R.color.colorE0)
			binding.payOffline.invalidate()
		}

		binding.payOffline.setOnClickListener {
			mViewModel.checkoutPrices.paymentType = 2
			binding.payOffline.strokeColor = findColor(R.color.colorPrimary)
			binding.payOffline.invalidate()
			binding.payOnline.strokeColor = findColor(R.color.colorE0)
			binding.payOnline.invalidate()
		}

		binding.orderNow.setOnClickListener {
			mViewModel.checkoutPrices.reserveTime = ""
			mViewModel.checkoutPrices.orderLater = mViewModel.shopCartTools.orderLater
			mViewModel.checkoutPrices.orderTime = 1
			mViewModel.lastReserveTimePosition = -1

			binding.orderNow.strokeColor = findColor(R.color.colorPrimary)
			binding.orderNow.invalidate()
			binding.orderLater.strokeColor = findColor(R.color.colorE0)
			binding.orderLater.invalidate()
		}

		binding.orderLater.setOnClickListener {
			if (!mViewModel.checkoutPrices.isReserve) {
				return@setOnClickListener
			}
			val items = mutableListOf<String>()
			for (reserveTime in mViewModel.shopCartTools.reserveTimes) {
				items.add(reserveTime.rTime)
			}
			MaterialDialog(requireContext()).apply {
				title(text = mViewModel.shopCartTools.orderLater)
				listItemsSingleChoice(
					items = items,
					initialSelection = mViewModel.lastReserveTimePosition
				) { dialog, index, text ->
					if (mViewModel.lastReserveTimePosition >= 0) {
						mViewModel.shopCartTools.reserveTimes[mViewModel.lastReserveTimePosition].isSelected =
							false
					}
					mViewModel.lastReserveTimePosition = index
					mViewModel.shopCartTools.reserveTimes[mViewModel.lastReserveTimePosition].isSelected =
						true
					mViewModel.checkoutPrices.reserveTime =
						mViewModel.shopCartTools.reserveTimes[mViewModel.lastReserveTimePosition].rTime
					mViewModel.checkoutPrices.orderLater = "زمان انتخابی: $text"
					mViewModel.checkoutPrices.orderTime = 2
					dialog.dismiss()

					binding.orderNow.strokeColor = findColor(R.color.colorE0)
					binding.orderNow.invalidate()
					binding.orderLater.strokeColor = findColor(R.color.colorPrimary)
					binding.orderLater.invalidate()
				}
				show()
			}
		}


		val selectedCity = loadFromSp<CityModel>(SELECTED_CITY_MODEL, CityModel())
		if (selectedCity.id > 0) {
			binding.txtCity.visibility = View.VISIBLE
			binding.txtCity.text = selectedCity.name
		} else {
			binding.txtCity.visibility = View.GONE
		}
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etDiscount.clearFocus()
			binding.tilDiscount.clearFocus()
			binding.etDescription.clearFocus()
		}
	}


	override fun handleNotification() {

		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		d("wallet check out, ${key} ${additional}", "wallet")
		if (key == "wallet" && additional.isNotEmpty()) {
			try {
				val additionalObject = mViewModel.gson.fromJson(additional, JsonObject::class.java)
				mViewModel.shopCartTools.wallet = additionalObject["amount"].asInt
				binding.viewModel = mViewModel

			} catch (ex: Exception) {
				eToast(ex.message)
			} finally {
				deleteFromSp(NOTIFICATION_KEY)
				deleteFromSp(NOTIFICATION_ADDITIONAL)
			}
		}
	}

	override fun handlePayment() {
		mViewModel.isFromBrowser = true
		mViewModel.checkPayment()
	}

	override fun onResume() {
		super.onResume()
		binding.etDiscount.clearFocus()
		binding.etDescription.clearFocus()
		if (!mViewModel.isFromBrowser) {
			mViewModel.checkPayment()
		}
	}

	private fun calculatePrice() {
		mViewModel.checkoutPrices.reset()
		mViewModel.mListItems.forEach { shopCartItem ->
			mViewModel.checkoutPrices.orderSum += (shopCartItem.price * shopCartItem.qty)
			mViewModel.checkoutPrices.productDiscount += (shopCartItem.discount * shopCartItem.qty)
		}

		//region Tax And Toll
		val tax = loadFromSp<Int>("tax", 0)
		val toll = loadFromSp<Int>("toll", 0)
		mViewModel.checkoutPrices.tax =
			((mViewModel.checkoutPrices.orderSum - mViewModel.checkoutPrices.productDiscount) * tax / 100)
		mViewModel.checkoutPrices.toll =
			((mViewModel.checkoutPrices.orderSum - mViewModel.checkoutPrices.productDiscount) * toll / 100)
		//endregion

		//region Discount Code
		if (mViewModel.checkoutPrices.discountCode.isNotEmpty()) {
			if (mViewModel.checkoutPrices.discountPercent > 0) {
				val totalToPay =
					mViewModel.checkoutPrices.orderSum + mViewModel.checkoutPrices.tax + mViewModel.checkoutPrices.toll - mViewModel.checkoutPrices.productDiscount
				val discountAmount = totalToPay * mViewModel.checkoutPrices.discountPercent / 100
				if (discountAmount > mViewModel.checkoutPrices.discountMaxAmount) {
					mViewModel.checkoutPrices.discount = mViewModel.checkoutPrices.discountMaxAmount
				} else {
					mViewModel.checkoutPrices.discount = discountAmount
				}
				binding.etDiscount.isEnabled = false
				binding.btnDiscount.text = findString(R.string.mDeleteDiscount)
			} else {
				mViewModel.checkoutPrices.discount = 0
				mViewModel.checkoutPrices.discountPercent = 0
				mViewModel.checkoutPrices.discountMaxAmount = 0
				mViewModel.checkoutPrices.discountCode = ""
				binding.etDiscount.setText(null)
				binding.etDiscount.isEnabled = true
				binding.btnDiscount.text = findString(R.string.mCheck)
			}
		} else {
			mViewModel.checkoutPrices.discount = 0
			mViewModel.checkoutPrices.discountPercent = 0
			mViewModel.checkoutPrices.discountMaxAmount = 0
			mViewModel.checkoutPrices.discountCode = ""
			binding.etDiscount.setText(null)
			binding.etDiscount.isEnabled = true
			binding.btnDiscount.text = findString(R.string.mCheck)
			binding.btnDiscount.setBackgroundResource(R.drawable.bg_progress_success)
		}
		//endregion

		//region Wallet
		if (mViewModel.isWallet == 1) {
			val totalToPay =
				mViewModel.checkoutPrices.orderSum + mViewModel.checkoutPrices.tax + mViewModel.checkoutPrices.toll + mViewModel.checkoutPrices.courier_price - mViewModel.checkoutPrices.productDiscount - mViewModel.checkoutPrices.discount
			val userWallet = AppObject.userInfo.wallet

			if (userWallet >= totalToPay) {
				mViewModel.checkoutPrices.walletAmount = totalToPay
			} else {
				mViewModel.checkoutPrices.walletAmount = userWallet
			}
		} else {
			mViewModel.checkoutPrices.walletAmount = 0
		}
		//endregion

		//check if total pay is 0 set payment type to offline
		if (mViewModel.checkoutPrices.totalToPay == 0) {
			mViewModel.checkoutPrices.paymentType = 2
			binding.payOnline.strokeColor = findColor(R.color.colorE0)
			binding.payOnline.invalidate()
			binding.payOffline.strokeColor = findColor(R.color.colorPrimary)
			binding.payOffline.invalidate()
		}
	}

	private fun mToolsObserver() {
		baseObserver(
			this,
			mViewModel.mToolsObserver,
			object : RequestConnectionResult<ShopCartTools> {
				override fun onProgress(loading: Boolean) {
					setProgressView(binding.mainView, loading, 2)
				}

				override fun onSuccess(data: ShopCartTools) {
					mViewModel.shopCartTools = data
					d(
						"data.is_reserve : mViewModel.shopCartTools  : ${mViewModel.shopCartTools}\n",
						"TaG"
					)
					mViewModel.setPaymentsAndReserve()
					AppObject.userInfo = loadFromSp(USER_INFO, UserInfo())
					AppObject.userInfo.wallet = data.wallet
					saveToSp(USER_INFO, AppObject.userInfo)

					calculatePrice()

					if (mViewModel.checkoutPrices.orderTime == 1) {
						binding.orderNow.visibility = View.GONE
					}
				}

				override fun onFailed(errorMessage: String) {
				}
			},
			4
		)
	}

	private fun mDiscountObserver() {
		baseObserver(this, mViewModel.mDiscountObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, type = 2)
			}

			override fun onSuccess(data: Msg) {
				mViewModel.checkoutPrices.discountPercent = data.percent
				mViewModel.checkoutPrices.discountMaxAmount = data.maxAmount
				calculatePrice()
			}

			override fun onFailed(errorMessage: String) {
			}
		}, 1)
	}

	private fun makeOrderJson() {
		val products = mutableListOf<OrderItemJson>()
		val pizzas = mutableListOf<OrderItemJson>()

		mViewModel.mListItems.forEach { shopCartItem ->
			val orderItemJson = OrderItemJson()
			orderItemJson.apply {
				id = shopCartItem.productID
				qty = shopCartItem.qty
				name = shopCartItem.name
				type = shopCartItem.type // add type to determine offer from other kinds of request
			}
			if (shopCartItem.pizza == 0) {
				val listType: Type = object : TypeToken<MutableList<ProductMaterial>>() {}.type
				val productMaterials =
					gson.fromJson<MutableList<ProductMaterial>>(shopCartItem.materials, listType)
				orderItemJson.materials = productMaterials
				products.add(orderItemJson)
			} else {
				pizzas.add(orderItemJson)
				val pizzaMaterials =
					gson.fromJson<BreadJson>(shopCartItem.materials, BreadJson::class.java)
				orderItemJson.materials = pizzaMaterials
			}
		}
		val orderJson = OrderJson(products = products, pizzas = pizzas)
		mViewModel.orderJson = gson.toJson(orderJson)
	}

	private fun mAddOrderObserver() {
		baseObserver(this, mViewModel.mOrderAddObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading)
			}

			override fun onSuccess(data: Msg) {

				if (data.link.isNotEmpty()) {
					mViewModel.mOrderAdd = data
					startAction(data.link)
				} else {
					val bundle = Bundle()
					bundle.apply {
						putString("mobile", AppObject.userInfo.mobile)
						putString("total_price", mViewModel.checkoutPrices.totalToPay.toString())
					}
					baseFragmentCallback?.logFirebase("user_purchase_shop_cart", bundle)

					GO_TO_ORDERS = true
					//Send order notice
					mViewModel.orderNotice(data.id)
					appDataBase.shopDao().deleteAll()
					removeUntil(R.id.shopCartFragment)
				}
			}

			override fun onFailed(msg: Msg) {
				when (msg.part) {
					//region Area Error
					"area" -> {
						MaterialDialog(requireContext())
							.apply {
								title(R.string.orderAddError)
								message(text = msg.msg)
								positiveButton(R.string._yes)
									.positiveButton {
										mViewModel.forceAddAddress = 1
										mViewModel.addOrder()
									}
								negativeButton(R.string.close)
								show()
							}
					}
					"area2" -> {
						MaterialDialog(requireContext())
							.apply {
								title(R.string.orderAddError)
								message(text = msg.msg)
								positiveButton(R.string.close)
								show()
							}
					}
					//endregion

					//region Time Error
					"time" -> {
						MaterialDialog(requireContext())
							.apply {
								title(R.string.orderAddError)
								message(text = msg.msg)
								positiveButton(R.string._yes)
									.positiveButton {
										mViewModel.forceAddTime = 1
										mViewModel.addOrder()
									}
								negativeButton(R.string.close)
								show()
							}
					}
					"time2" -> {
						MaterialDialog(requireContext())
							.apply {
								title(R.string.orderAddError)
								message(text = msg.msg)
								positiveButton(R.string.close)
								show()
							}
					}
					//endregion

					"entity" -> {
						MaterialDialog(requireContext())
							.apply {
								title(R.string.orderAddError)
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

	private fun mCheckPaymentObserver() {
		baseObserver(
			this,
			mViewModel.mOrderCheckPaymentObserver,
			object : RequestConnectionResult<Msg> {
				override fun onProgress(loading: Boolean) {
					setProgressView(binding.mainView, loading)
				}

				override fun onSuccess(data: Msg) {
					if (mViewModel.isFromBrowser) {
						mViewModel.isFromBrowser = false
						mViewModel.mOrderAdd = null
					}
					if (data.paymentStatus == 0) {
						eToast(data.msg)
					} else {
						val bundle = Bundle()
						bundle.apply {
							putString("mobile", AppObject.userInfo.mobile)
							putString("total_price", mViewModel.checkoutPrices.totalToPay.toString())
						}
						baseFragmentCallback?.logFirebase("user_purchase_shop_cart", bundle)

						mViewModel.mOrderAdd = null
						GO_TO_ORDERS = true
						sToast(data.msg)
						//Send order notice
						mViewModel.orderNotice(data.id)
						appDataBase.shopDao().deleteAll()
						removeUntil(R.id.shopCartFragment)
					}
				}

				override fun onFailed(msg: Msg) {
					MaterialDialog(requireContext())
						.apply {
							noAutoDismiss()
							cancelOnTouchOutside(false)
							cancelable(false)
							title(R.string._error_in_connection)
							message(text = msg.msg)
							positiveButton(R.string._retry)
								.positiveButton {
									mViewModel.checkPayment()
								}
							show()
						}
				}
			},
			5
		)
	}
}