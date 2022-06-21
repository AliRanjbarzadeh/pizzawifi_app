package ir.atriatech.pizzawifi.ui.main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import io.reactivex.Completable
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.*
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.performOnBackOutOnMain
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.databinding.FragmentOrdersBinding
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.OrderItem
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import java.lang.reflect.Type


class OrdersFragment : BaseFragment(), RecyclerViewTools, OnRefreshListener, IsEndOfRecyclerView {
	lateinit var binding: FragmentOrdersBinding
	private lateinit var mViewModel: OrdersFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(OrdersFragmentViewModel::class.java)
		mObserver()
		mReorderObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)

		binding.lifecycleOwner = this
		binding.viewModel = mViewModel
		hideKeyboard(binding.rvOrders)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.getData(limit, offset)) {
			mViewModel.isFirst = false
			setAdapter()
		}

		/*
		binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
			var keyAction = false
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				keyAction = when {
					mViewModel.mSearchText.isEmpty() -> {
						eToast(findString(R.string.enterOrderId))
						true
					}
					else -> {
						binding.imgSearch.performClick()
						false
					}
				}
			}
			keyAction
		}

		binding.imgSearch.setOnClickListener {
			if (mViewModel.isRefreshing) {
				return@setOnClickListener
			}
			if (mViewModel.mSearchText.isEmpty()) {
				eToast(findString(R.string.enterOrderId))
				return@setOnClickListener
			}
			mViewModel.mSearchText = binding.etSearch.text.toString()
			mViewModel.mListItems.clear()
			mViewModel.mAdapter?.list?.clear()
			offset = 0
			mViewModel.isSearching = true
			mViewModel.getData(limit, offset)
		}
		*/

		//Check end of data
		checkRvEnd(binding.rvOrders, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)
		baseFragmentCallback?.changeSoftkeyMethod(true)
	}

	override fun onResume() {
		super.onResume()
		/*
		binding.etSearch.afterDelayTextChanged({
			if (it.isEmpty()) {
				binding.txtEmptyOrders.visibility = View.GONE
				mViewModel.mListItems.clear()
				mViewModel.mAdapter?.list?.clear()
				mViewModel.isSearching = true
				offset = 0
				mViewModel.getData(limit, offset)
			}
		}, 300, requireActivity())

		*/
		if (GO_TO_ORDERS) {
			GO_TO_ORDERS = false
			if (!mViewModel.isFirst) {
				offset = 0
				mViewModel.isRefreshing = true
				mViewModel.isEmptyView.set(false)
				mViewModel.isShowContent.set(false)
				mViewModel.mListItems.clear()
				mViewModel.mAdapter?.list?.clear()
				mViewModel.getData(limit, offset)
			}
		}
		if (GO_SHOP_CART) {
			GO_SHOP_CART = false
			baseFragmentCallback?.goToShopCart()
		}
	}

	override fun onDestroyView() {
		baseFragmentCallback?.changeSoftkeyMethod(false)
		super.onDestroyView()
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			//etSearch.clearFocus()
		}
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		if (key.equals("orders") && additional.isNotEmpty()) {
			try {
				val msg = gson.fromJson<Msg>(additional, Msg::class.java)
				val arg = extraArguments(msg.id, ARG_STRING_1)
				navTo(R.id.orderDetailFragment, arg = arg)
			} catch (ex: Exception) {
				eToast(ex.message)
			} finally {
				deleteFromSp(NOTIFICATION_KEY)
				deleteFromSp(NOTIFICATION_ADDITIONAL)
			}
		}
	}

	override fun updateValues() {


		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		if (key == "orders" && additional.isNotEmpty()) {
			try {
				val additionalObject = mViewModel.gson.fromJson(additional, JsonObject::class.java)

				if (mViewModel.mListItems.isNotEmpty()) {
					var updatePosition = -1
					Completable.fromAction {
						loop@ for (i in 0 until mViewModel.mListItems.size) {
							if (mViewModel.mListItems[i] != null) {
								if (mViewModel.mListItems[i].id == additionalObject["id"].asInt) {
									updatePosition = i
									mViewModel.mListItems[i].orderStatus =
										additionalObject["order_status"].asInt
									mViewModel.mListItems[i].statusText =
										additionalObject["status_text"].asString
									mViewModel.mListItems[i].declineReason =
										additionalObject["decline_reason"].asString
									mViewModel.mAdapter?.list?.set(i, mViewModel.mListItems[i]!!)
									break@loop
								}
							}
						}
					}.performOnBackOutOnMain(mViewModel.scheduler)
						.doOnComplete {
							val firstItem =
								(binding.rvOrders.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
							val lastItem =
								(binding.rvOrders.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
							if (updatePosition in firstItem..lastItem)
								mViewModel.mAdapter?.notifyItemChanged(updatePosition)
						}.subscribe()
				}
			} catch (e: Exception) {
			}
		}
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as Order
		when (view.id) {
			R.id.btnReorder -> {
				mViewModel.getReorderData(item.id)
			}

			R.id.btnSurvey -> {
				val arg = extraArguments(argument1 = item, key1 = ARG_STRING_1)
				navTo(R.id.surveyFragment, arg = arg)
			}

			else -> {
				val arg = extraArguments(item.id, ARG_STRING_1)
				navTo(R.id.orderDetailFragment, arg = arg)
			}
		}
	}

	override fun onRefresh(refreshLayout: RefreshLayout) {
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.isShowContent.set(false)
		mViewModel.isEmptyView.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		mViewModel.getData(limit, offset)
	}

	override fun onReachToEnd() {
		if (!mViewModel.isEnd) {
			mViewModel.isLoadMore = true
			mViewModel.getData(limit, offset)
		}
	}

	fun hardRefresh() {
//		onRefresh(binding.refreshLayout)
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.isShowContent.set(false)
		mViewModel.isEmptyView.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		mViewModel.getData(limit, offset)
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = OrdersAdapter(this.requireContext(), this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (offset == 0 && mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (mViewModel.isSearching) {
			mViewModel.isSearching = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		if (!mViewModel.isLoadMore) {
			try {
				binding.rvOrders.removeItemDecorationAt(0)
			} catch (e: Exception) {
			}
			binding.rvOrders.setHasFixedSize(true)
			binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
			binding.rvOrders.addItemDecoration(MarginItemDecoration(dp2px(2), marginPosition = 2))
			binding.rvOrders.adapter = mViewModel.mAdapter
		} else if (mViewModel.mLoadMoreItems.isNotEmpty()) {
			mViewModel.isLoadMore = false
			mViewModel.mListItems.addAll(mViewModel.mLoadMoreItems)
			val startPosition = mViewModel.mAdapter!!.list.size
			mViewModel.mAdapter!!.addToList(mViewModel.mLoadMoreItems)
			mViewModel.mAdapter!!.notifyItemRangeInserted(
				startPosition,
				mViewModel.mLoadMoreItems.size
			)
			mViewModel.mLoadMoreItems.clear()
		}
		handleNotification()
	}

	private fun mObserver() {
		baseListObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<Order>> {
				override fun onProgress(loading: Boolean) {
					if (loading && mViewModel.mListItems.isEmpty()) {
						mViewModel.isShowContent.set(false)
						mViewModel.isEmptyView.set(false)
						binding.rvOrders.showShimmerAdapter()
//                    if (!mViewModel.isRefreshing) {
//                        binding.rvOrders.showShimmerAdapter()
//                    }
					} else if (mViewModel.mListItems.isNotEmpty()) {
						binding.refreshLayout.setEnableLoadMore(loading)
					} else {
						binding.rvOrders.hideShimmerAdapter()
					}
					/*
					if (loading) {
						binding.etSearch.isEnabled = false
					} else {
						binding.etSearch.isEnabled = true
					}
					 */
				}

				override fun onSuccess(data: MutableList<Order>) {
					binding.refreshLayout.finishRefresh()
					binding.refreshLayout.finishLoadMore()
					if (!mViewModel.isLoadMore) {
						mViewModel.mListItems = data
						if (mViewModel.mSearchText.isEmpty()) {
							mViewModel.isEmptyView.set(data.size == 0)
							mViewModel.isSearchBox.set(data.size != 0)
						} else {
							if (data.size == 0) {
								binding.txtEmptyOrders.visibility = View.VISIBLE
								binding.txtEmptyOrders.setText(findString(R.string.noOrderFoundSearch))
							}
						}
						mViewModel.isShowContent.set(data.size != 0)
					} else if (data.isNotEmpty()) {
						mViewModel.mLoadMoreItems = data
					}
					mViewModel.isEnd = limit > data.size
					setAdapter()
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.getData(limit, offset)
				}
			},
			4
		)
	}

	private fun mReorderObserver() {
		baseObserver(this, mViewModel.mReorderObserver, object : RequestConnectionResult<Order> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Order) {
				d(data, "OrderDetailInfo")
				if (data.isReorder) {
					mDialog?.dismiss()
					mDialog = MaterialDialog(requireContext())
						.apply {
							title(R.string.reorder)
							message(R.string.reorderDescription)
							positiveButton(R.string._yes)
								.positiveButton {
									appDataBase.shopDao().deleteAll()
									reorder(data)
								}
							negativeButton(R.string._no)
							show()
						}
				} else {
					eToast(getString(R.string.reorderError))
				}
			}
		})
	}

	private fun reorder(order: Order) {
		try {
			val listType: Type = object : TypeToken<MutableList<OrderItem>>() {}.type
			val orderItems = gson.fromJson<MutableList<OrderItem>>(
				order.orderItems,
				listType
			)
			val items = mutableListOf<ShopCartItem>()
			orderItems.forEach { orderItem ->
				val shopCartItem = ShopCartItem()
				shopCartItem.apply {
					productID = orderItem.id
					name = orderItem.name
					price = orderItem.updatePrice
					type = orderItem.type
					max_choice = orderItem.max_choice
					branchId = order.branch.id
					branchName = order.branch.name
					branchAddress = order.branch.address
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
			saveToSp(SELECTED_CITY_MODEL, order.city)
			AppObject.selectedCityId = order.city.id
			AppObject.cityItem = order.city

			//Save branch in session and AppObject
			saveToSp(SELECTED_BRANCH_MODEL, order.city.branches.first())

			//Make home refresh after select branch
			if (AppObject.selectedBranchId != order.city.branches.first().id) {
				SHOULD_REFRESH_HOME = true
			}
			AppObject.selectedBranchId = order.city.branches.first().id
			AppObject.branchItem = order.city.branches.first()
			//endregion

			baseFragmentCallback?.goToShopCart()
		} catch (ex: Exception) {
			eToast(getString(R.string.reorderError))
		}
	}
}