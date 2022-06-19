package ir.atriatech.pizzawifi.ui.main.profile.address

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
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.AddressToolsBinding
import ir.atriatech.pizzawifi.databinding.FragmentAddressBinding
import ir.atriatech.pizzawifi.entities.profile.address.Address


class AddressFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView, OnRefreshListener {
	lateinit var binding: FragmentAddressBinding
	private lateinit var mViewModel: AddressFragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(AddressFragmentViewModel::class.java)
		mObserver()
		mDeleteObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { back() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.btnAdd2.setOnClickListener { binding.fabAdd.callOnClick() }
		binding.fabAdd.setOnClickListener {
			val address =
				Address(name = AppObject.userInfo.name, mobile = AppObject.userInfo.mobile)
			val arg = extraArguments(address, ARG_STRING_1)
			navTo(R.id.addressMapFragment, arg = arg)
		}
		if (mViewModel.getData(limit, offset)) {
			setAdapter()
		}

		//Check end of data
		checkRvEnd(binding.rvAddress, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)
	}

	override fun handleNotification() {
		mDialog?.cancel()
		removeUntil(R.id.profileFragment)
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
				argument3 = false,
				key3 = ARG_STRING_3
			)
			navTo(R.id.addressMapFragment, arg = arg)
		}
		addressToolsBinding.executePendingBindings()

		mDialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).apply {
			customView(view = addressToolsBinding.root)
			cornerRadius(res = R.dimen._20mdp)
			show()
		}
	}


	override fun onRefresh(refreshLayout: RefreshLayout) {
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.isShowContent.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		binding.rvAddress.adapter = null
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
	}

	override fun <T> editItemInList(mItem: T, position: Int) {
		mItem as Address
		d(mItem, "Address")
		d(mViewModel.mListItems, "Address")
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
				mViewModel.mListItems.removeAt(position)
				mViewModel.mAdapter?.list?.removeAt(position)
				mViewModel.mAdapter?.notifyItemRemoved(position)
				mViewModel.isEmptyView.set(mViewModel.mListItems.isEmpty())
				mViewModel.isShowContent.set(mViewModel.mListItems.isNotEmpty())
				break
			}
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = AddressAdapter(this)
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
			binding.rvAddress.addItemDecoration(
				MarginItemDecoration(
					dp2px(1),
					marginPosition = 2
				)
			)
			binding.rvAddress.isLongPressDragEnabled = false
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

	private fun mDeleteObserver() {
		baseObserver(this, mViewModel.mDeleteObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Msg) {
				mDialog = null
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