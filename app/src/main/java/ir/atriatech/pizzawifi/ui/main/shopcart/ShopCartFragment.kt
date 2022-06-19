package ir.atriatech.pizzawifi.ui.main.shopcart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.muddzdev.styleabletoast.StyleableToast
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.app.app
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.extensions.kotlin.spannableString
import ir.atriatech.extensions.reactivex.performOnMainDoInBack
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.DELIVER_TYPE
import ir.atriatech.pizzawifi.common.GO_TO_ORDERS
import ir.atriatech.pizzawifi.common.SHOP_CART
import ir.atriatech.pizzawifi.databinding.FragmentShopCartBinding
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.ui.login.LoginActivity


class ShopCartFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentShopCartBinding
	private lateinit var mViewModel: ShopCartFragmentViewModel
	var productCountToast: StyleableToast? = null

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ShopCartFragmentViewModel::class.java)

		appDataBase.shopDao().countAllAndPrices()
			.performOnMainDoInBack()
			.doAfterNext {
				if (it == null) {
					mViewModel.isEmptyView.set(true)
					mViewModel.isShowContent.set(false)
				} else if (it.totalRows == 0) {
					mViewModel.isEmptyView.set(true)
					mViewModel.isShowContent.set(false)
				} else {
					var price = it.orderSum - it.totalDiscount
					val tax = loadFromSp<Int>("tax", 0)
					val toll = loadFromSp<Int>("toll", 0)
					price += (price * tax / 100)
					price += (price * toll / 100)
					mViewModel.mPrice.set(price.priceFormat())
				}
			}
			.subscribe()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop_cart, container, false)

		binding.lifecycleOwner = this
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		mViewModel.mListItems = appDataBase.shopDao().getAll()

		mViewModel.isEmptyView.set(mViewModel.mListItems.size == 0)
		mViewModel.isShowContent.set(mViewModel.mListItems.size != 0)
		setAdapter()

		binding.imgDelete.setOnClickListener {
			MaterialDialog(requireContext())
				.apply {
					title(R.string.clearShopCartTitle)
					message(R.string.clearShopCartDescription)
					positiveButton(R.string._yes)
						.positiveButton {
							appDataBase.shopDao().deleteAll()
							mViewModel.mListItems.clear()
							mViewModel.isEmptyView.set(mViewModel.mListItems.size == 0)
							mViewModel.isShowContent.set(mViewModel.mListItems.size != 0)
							mViewModel.mAdapter?.list?.clear()
							mViewModel.mAdapter?.notifyDataSetChanged()
						}
					negativeButton(R.string._no)
					show()
				}
		}

		val getLogin = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
			when {
				activityResult.resultCode == Activity.RESULT_OK && activityResult.data != null -> {
					d(AppObject.userInfo)
					baseFragmentCallback?.changeMenusForLogin()
					//Continue next step of shopcart
					binding.btnNext.callOnClick()
				}
			}
		}

		binding.btnNext.setOnClickListener {
			if (mViewModel.isEmptyView.get()) {
				return@setOnClickListener
			}

			if (!AppObject.userInfo.isLogin) {
				//go to login activity with shopCart tag
				val loginIntent = Intent(requireContext(), LoginActivity::class.java)
				getLogin.launch(loginIntent)
				return@setOnClickListener
			}

			if (AppObject.selectedBranchId == 0) {
				// no branch selescted
				val arg = extraArguments(argument1 = SHOP_CART, key1 = ARG_STRING_1)
				navTo(R.id.cityFragment, arg = arg)
				return@setOnClickListener
			}

			when (baseFragmentCallback?.getHomeRemoteModel()?.homeExtra?.goAddress) {
				1 -> {
					DELIVER_TYPE = 3
					navTo(R.id.shopCartAddressFragment)
				}

				else -> navTo(R.id.shopCartDecideFragment)
			}
		}
	}

	override fun onResume() {
		super.onResume()
		if (GO_TO_ORDERS) {
			baseFragmentCallback?.goToOrders()
		}
	}

	override fun <T> onIncreaseItem(position: Int, view: View, item: T) {
		item as ShopCartItem

		d("item type ==> ${item.type} ${item.name}  ${item.id}")
		if (item.type == 3 && item.qty == item.max_choice) {
			showEToast(
				String.format(
					getString(R.string.maxChoiceProduct),
					item.max_choice,
					item.name
				)
			)
			return
		}

		item.qty++
		appDataBase.shopDao().updateItem(item)

		when {
			item.qty == 1 -> {
				(view.findViewById(R.id.btnDecrease) as AppCompatImageView).setImageResource(R.drawable.ic_trash2)
			}
			item.qty > 1 -> {
				(view.findViewById(R.id.btnDecrease) as AppCompatImageView).setImageResource(R.drawable.ic_baseline_remove_24)
			}
		}
	}

	override fun <T> onDecreaseItem(position: Int, view: View, item: T) {
		item as ShopCartItem
		if (item.qty == 1) {
			onDeleteClick(position, view, item)
			return
		}
		item.qty--
		appDataBase.shopDao().updateItem(item)

		when {
			item.qty == 1 -> {
				(view.findViewById(R.id.btnDecrease) as AppCompatImageView).setImageResource(R.drawable.ic_trash2)
			}
			item.qty > 1 -> {
				(view.findViewById(R.id.btnDecrease) as AppCompatImageView).setImageResource(R.drawable.ic_baseline_remove_24)
			}
		}
	}

	override fun <T> onDeleteClick(position: Int, view: View, item: T) {
		item as ShopCartItem
		val deleteDescription = spannableString(
			firstString = "آیا برای حذف ",
			secondString = item.name,
			secondFont = findString(R.string.app_font_bold),
			secondColor = R.color.textPrimary,
			thirdString = " از سبد خرید اطمینان دارید؟"
		)
		if (mDialog == null || mDialog?.isShowing == false) {
			mDialog = MaterialDialog(requireContext())
				.apply {
					title(R.string.deleteFromShopCartTitle)
					message(text = deleteDescription)
					positiveButton(R.string._yes)
						.positiveButton {
							appDataBase.shopDao().deleteItem(item)
							mViewModel.mListItems.removeAt(position)
							mViewModel.mAdapter?.notifyItemRemoved(position)
						}
					negativeButton(R.string._no)
					show()
				}
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = ShopCartAdapter(this)
		}
		mViewModel.mAdapter!!.list = mViewModel.mListItems

		try {
			binding.rvShopCart.removeItemDecorationAt(0)
		} catch (ex: Exception) {
		}
		binding.rvShopCart.setHasFixedSize(true)
		binding.rvShopCart.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		binding.rvShopCart.addItemDecoration(
			MarginItemDecoration(
				dp2px(8),
				MarginItemDecoration.TOP
			)
		)
		binding.rvShopCart.adapter = mViewModel.mAdapter
	}

	fun showEToast(msg: Any?) {
		if (productCountToast != null) {
			productCountToast?.cancel()
		}
		productCountToast = StyleableToast.makeText(
			app,
			msg.toString(),
			Toast.LENGTH_LONG,
			ir.atriatech.extensions.R.style.eToast
		)
		productCountToast?.show()
	}

	private fun checkListChanges() {
		val difResult = DiffUtil.calculateDiff(
			ShopCartItemDiffUtil(
				mViewModel.mAdapter!!.list,
				mViewModel.mListItems
			)
		)
		difResult.dispatchUpdatesTo(object : ListUpdateCallback {
			override fun onChanged(position: Int, count: Int, payload: Any?) {
				for (i in position until count) {
					mViewModel.mAdapter?.list?.get(i)?.qty = mViewModel.mListItems[i].qty
					mViewModel.mAdapter?.notifyItemChanged(i)
				}
			}

			override fun onMoved(fromPosition: Int, toPosition: Int) {
				d(fromPosition, "ShopCart onMoved")
				d(toPosition, "ShopCart onMoved")
			}

			override fun onInserted(position: Int, count: Int) {
				mViewModel.mAdapter?.list?.addAll(
					position,
					mViewModel.mListItems.subList(position, count)
				)
				mViewModel.mAdapter?.notifyItemRangeInserted(position, count)
			}

			override fun onRemoved(position: Int, count: Int) {
				for (i in position until count) {
					mViewModel.mAdapter?.list?.removeAt(i)
				}
				mViewModel.mAdapter?.notifyDataSetChanged()
			}

		})
//		if (mViewModel.mListItems != mViewModel.mAdapter?.list) {
//			mViewModel.mAdapter?.list?.clear()
//			mViewModel.mAdapter?.addToList(mViewModel.mListItems)
//		}
	}
}