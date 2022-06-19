package ir.atriatech.pizzawifi.ui.main.profile.wallet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.android.ui.formatPrice
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.*
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.NOTIFICATION_ADDITIONAL
import ir.atriatech.pizzawifi.common.NOTIFICATION_KEY
import ir.atriatech.pizzawifi.databinding.FragmentWalletBinding
import ir.atriatech.pizzawifi.entities.profile.wallet.Wallet

class WalletFragment : BaseFragment(), IsEndOfRecyclerView, OnRefreshListener {
	lateinit var binding: FragmentWalletBinding
	private lateinit var mViewModel: WalletFragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(WalletFragmentViewModel::class.java)
		mObserver()
		mIncreaseObserver()
		mCheckouteObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		hideKeyboard(binding.mainView)
		hideKeyboard(binding.rvWallet)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.getData(limit, offset)) {
			setAdapter()
		}

		//Check end of data
		checkRvEnd(binding.rvWallet, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)

		binding.etAmount.formatPrice()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		baseFragmentCallback?.changeSoftkeyMethod(true)
	}

	override fun onDetach() {
		baseFragmentCallback?.changeSoftkeyMethod(false)
		super.onDetach()
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etAmount.clearFocus()
		}
	}

	override fun onRefresh(refreshLayout: RefreshLayout) {
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		try {
			binding.rvWallet.removeItemDecorationAt(0)
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

	override fun handlePayment() {
		mViewModel.isFromBrowser = true
		mViewModel.checkout()
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		when (key) {
			"wallet" -> {
				onRefresh(binding.refreshLayout)
				deleteFromSp(NOTIFICATION_KEY)
				deleteFromSp(NOTIFICATION_ADDITIONAL)
			}

			"orders" -> {
			}

			else -> removeUntil(R.id.profileFragment)
		}
	}

	override fun onResume() {
		super.onResume()
		if (!mViewModel.isFromBrowser) {
			mViewModel.checkout()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		binding.btnSend.dispose()
	}

	fun isEmptyList() = mViewModel.isEmptyView.get()

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = WalletAdapter(object : RecyclerViewTools {})
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (offset == 0 && mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		if (!mViewModel.isLoadMore) {

			try {
				binding.rvWallet.removeItemDecorationAt(0)
			} catch (e: Exception) {
			}

			binding.rvWallet.setHasFixedSize(true)
			binding.rvWallet.layoutManager = LinearLayoutManager(requireContext())
			binding.rvWallet.addItemDecoration(MarginItemDecoration(dp2px(1), marginPosition = 2))
			binding.rvWallet.adapter = mViewModel.mAdapter
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

		if (AppObject.isShowKeyboard) {
			AppObject.isShowKeyboard = false
			binding.etAmount.postDelayed({ showInputMethod(binding.etAmount) }, 100)
		}
	}


	private fun mObserver() {
		baseListObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<Wallet>> {
				override fun onProgress(loading: Boolean) {
					if (!mViewModel.isRefreshing && !mViewModel.isLoadMore)
						setProgressView(binding.mainView, loading, 2)
				}

				override fun onSuccess(data: MutableList<Wallet>) {
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

	private fun mIncreaseObserver() {
		baseObserver(this, mViewModel.mIncreaseObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				binding.btnSend.startAnimation()
			}

			override fun onSuccess(data: Msg) {
				try {
					binding.btnSend.revertAnimation()
					mViewModel.mChargeItem = data
					startAction(data.link)
				} catch (ex: Exception) {
					e(ex.message)
					eToast(findString(R.string.somethingWrong))
				}
			}

			override fun onFailed(errorMessage: String) {
				binding.btnSend.revertAnimation()
			}
		}, 1)
	}

	private fun mCheckouteObserver() {
		baseObserver(this, mViewModel.mCheckoutObserver, object : RequestConnectionResult<Wallet> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Wallet) {
				if (mViewModel.isFromBrowser) {
					mViewModel.isFromBrowser = false
					mViewModel.mChargeItem.id = -1
				}
				if (data.status == 0) {
					eToast(findString(R.string.paymentFailed))
				} else {
					binding.etAmount.setText("")
					binding.etAmount.clearFocus()
					mViewModel.mListItems.add(0, data)
					mViewModel.mAdapter?.list?.add(0, data)
					mViewModel.mAdapter?.notifyItemInserted(0)
					binding.rvWallet.smoothScrollToPosition(0)
					AppObject.userInfo.wallet += data.amount
					sToast(findString(R.string.paymentSuccess))
					mViewModel.mChargeItem.id = -1
				}
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.checkout()
			}
		}, 4)
	}
}