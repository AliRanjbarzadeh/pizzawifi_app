package ir.atriatech.pizzawifi.ui.main.shopcart.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.ARG_STRING_3
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.AddressToolsBinding
import ir.atriatech.pizzawifi.databinding.FragmentShopCartAddressBinding
import ir.atriatech.pizzawifi.entities.profile.address.Address

class ShopCartAddressFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView,
	OnRefreshListener {
	lateinit var binding: FragmentShopCartAddressBinding
	private lateinit var mViewModel: ShopCartAddressFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ShopCartAddressFragmentViewModel::class.java)
		mObserver()
		mDeleteObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_shop_cart_address, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }

		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		mViewModel.mListShopCart = appDataBase.shopDao().getAll()
		if (mViewModel.mListShopCart.isEmpty()) {
			removeUntil(R.id.shopCartFragment)
			return
		}

		binding.btnAdd2.setOnClickListener { binding.fabAdd.callOnClick() }
		binding.fabAdd.setOnClickListener {
			val address =
				Address(name = AppObject.userInfo.name, mobile = AppObject.userInfo.mobile)
			val arg = extraArguments(
				argument1 = address,
				key1 = ARG_STRING_1,
				argument3 = true,
				key3 = ARG_STRING_3
			)
			navTo(R.id.addressMapFragment2, arg = arg)
		}
		if (mViewModel.getData(limit, offset)) {
			setAdapter()
		}

		//Check end of data
		checkRvEnd(binding.rvAddress, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)

		binding.btnNext.setOnClickListener {
			if (mViewModel.lastSelectedPosition < 0) {
				eToast(findString(R.string.selectAddressError))
				return@setOnClickListener
			}
			val arg = extraArguments(
				argument1 = mViewModel.mListItems[mViewModel.lastSelectedPosition],
				key1 = ARG_STRING_1
			)
			navTo(R.id.shopCartCheckoutFragment, arg = arg)
		}
	}

	override fun onRefresh(refreshLayout: RefreshLayout) {
		mViewModel.lastSelectedPosition = -1
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.isShowContent.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		try {
			binding.rvAddress.removeItemDecorationAt(0)
		} catch (ex: java.lang.Exception) {

		}
		mViewModel.getData(limit, offset)
	}

	override fun onReachToEnd() {
		if (!mViewModel.isEnd) {
			mViewModel.isLoadMore = true
			mViewModel.getData(limit, offset)
		}
	}

	override fun <T> addItemToList(mItem: T) {
		mItem as Address
		mViewModel.mListItems.add(0, mItem)
		mViewModel.mAdapter?.list?.add(0, mItem)
		mViewModel.isEmptyView.set(false)
		mViewModel.isShowContent.set(true)

		if (mViewModel.lastSelectedPosition != -1) {
			mViewModel.lastSelectedPosition++
		}
	}

	override fun <T> editItemInList(mItem: T, position: Int) {
		mItem as Address
		for (i in 0 until mViewModel.mListItems.size) {
			if (mViewModel.mListItems[i].id == mItem.id) {
				mViewModel.mListItems[i].update(mItem)
				mViewModel.mAdapter?.list?.get(i)?.update(mItem)
				break
			}
		}
	}

	override fun <T> deleteItemFromList(mItem: T, position: Int) {
		mItem as Address
		for (i in 0 until mViewModel.mListItems.size) {
			if (mViewModel.mListItems[i].id == mItem.id) {

				if (i == mViewModel.lastSelectedPosition) {
					mViewModel.lastSelectedPosition = -1
				} else if (i < mViewModel.lastSelectedPosition && mViewModel.lastSelectedPosition != -1) {
					mViewModel.lastSelectedPosition--
				}
				mViewModel.mListItems.removeAt(i)
				mViewModel.mAdapter?.list?.removeAt(i)
				mViewModel.mAdapter?.notifyItemRemoved(i)
				mViewModel.isEmptyView.set(mViewModel.mListItems.isEmpty())
				mViewModel.isShowContent.set(mViewModel.mListItems.isNotEmpty())
				break
			}
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = ShopCartAddressAdapter(this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (offset == 0 && mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}


		if (!mViewModel.isLoadMore) {
			try {
				binding.rvAddress.removeItemDecorationAt(0)
			} catch (e: Exception) {
			}

			binding.rvAddress.adapter = null
			binding.rvAddress.setHasFixedSize(true)
			binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
			binding.rvAddress.addItemDecoration(MarginItemDecoration(dp2px(1), marginPosition = 2))
			binding.rvAddress.adapter = mViewModel.mAdapter
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
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as Address
		mDialog?.dismiss()

		val addressToolsBinding = DataBindingUtil.inflate<AddressToolsBinding>(layoutInflater, R.layout.address_tools, null, false)
		addressToolsBinding.item = item

		addressToolsBinding.imgClose.setOnClickListener { mDialog?.dismiss() }
		addressToolsBinding.btnDelete.setOnClickListener { onDeleteClick(position, view, item) }
		addressToolsBinding.btnEdit.setOnClickListener {
			val arg = extraArguments(
				argument1 = item,
				key1 = ARG_STRING_1,
				argument2 = position,
				key2 = ARG_STRING_2,
				argument3 = true,
				key3 = ARG_STRING_3
			)
			navTo(R.id.addressMapFragment2, arg = arg)
		}
		addressToolsBinding.executePendingBindings()

		mDialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).apply {
			customView(view = addressToolsBinding.root)
			cornerRadius(res = R.dimen._20mdp)
			show()
		}
	}

	override fun <T> onItemSelect(position: Int, view: View, item: T) {
		if (position == mViewModel.lastSelectedPosition)
			return
		if (mViewModel.lastSelectedPosition >= 0) {
			mViewModel.mListItems[mViewModel.lastSelectedPosition].isSelected = false
			mViewModel.mAdapter?.list?.get(mViewModel.lastSelectedPosition)?.isSelected = false
		}
		mViewModel.lastSelectedPosition = position
		mViewModel.mListItems[mViewModel.lastSelectedPosition].isSelected = true
		mViewModel.mAdapter?.list?.get(mViewModel.lastSelectedPosition)?.isSelected = true
	}

	override fun <T> onDeleteClick(position: Int, view: View, item: T) {
		val address = mViewModel.mListItems[position]
		mDialog?.dismiss()
		mDialog = MaterialDialog(requireContext())
			.apply {
				title(R.string.addressDeleteTittle)
				message(R.string.addressDeleteText)
				positiveButton(R.string._yes)
					.positiveButton {
						mViewModel.mDeleteId = address.id
						mViewModel.mDeletePosition = position
						mViewModel.delete()
					}
				negativeButton(R.string._no)
				show()
			}
	}

	private fun mObserver() {
		baseListObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<Address>> {
				override fun onProgress(loading: Boolean) {
					if (mViewModel.mListItems.isEmpty() && !mViewModel.isRefreshing) {
						setProgressView(binding.mainView, loading, 2)
					} else {
						binding.refreshLayout.setEnableLoadMore(loading)
					}
				}

				override fun onSuccess(data: MutableList<Address>) {
					binding.refreshLayout.finishRefresh()
					binding.refreshLayout.finishLoadMore()
					if (!mViewModel.isLoadMore) {
						mViewModel.mListItems = data
						mViewModel.isEmptyView.set(data.size == 0)
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

	private fun mDeleteObserver() {
		baseObserver(this, mViewModel.mDeleteObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Msg) {
				sToast(data.msg)
				baseFragmentCallback?.deleteItemFromList(
					mViewModel.mListItems[mViewModel.mDeletePosition],
					mViewModel.mDeletePosition
				)
				mViewModel.mDeleteId = -1
				mViewModel.mDeletePosition = -1
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.delete()
			}
		}, 4)
	}
}