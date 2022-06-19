package ir.atriatech.pizzawifi.ui.main.profile.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import io.reactivex.Completable
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.performOnBackOutOnMain
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.DELETE_NOTIF_KEYS
import ir.atriatech.pizzawifi.common.NOTIFICATION_ADDITIONAL
import ir.atriatech.pizzawifi.common.NOTIFICATION_KEY
import ir.atriatech.pizzawifi.databinding.FragmentSupportBinding
import ir.atriatech.pizzawifi.entities.profile.support.Support


class SupportFragment : BaseFragment(), RecyclerViewTools, OnRefreshListener, IsEndOfRecyclerView {
	lateinit var binding: FragmentSupportBinding
	private lateinit var mViewModel: SupportFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(SupportFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }

		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.getData(limit, offset)) {
			setAdapter()
		}

		binding.btnAdd.setOnClickListener { navTo(R.id.addSupportFragment) }
		binding.fabAdd.setOnClickListener { binding.btnAdd.callOnClick() }

		//Check end of data
		checkRvEnd(binding.rvSupport, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		when (key) {
			"support" -> {
				onRefresh(binding.refreshLayout)
				deleteFromSp(NOTIFICATION_KEY)
				deleteFromSp(NOTIFICATION_ADDITIONAL)
			}

			"orders" -> {
			}

			"" -> {
			}

			else -> removeUntil(R.id.profileFragment)
		}
	}


	override fun updateValues() {
		d("support main, =============", "support")
//        d("item support2222 ========== $")
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		d("item support2222 ========== key:  ${key}  additional ${additional}")
		if (key == "support" && additional.isNotEmpty()) {
			try {
//                d("item support2222 ========== try catch")

				val additionalObject = mViewModel.gson.fromJson(additional, JsonObject::class.java)
//                d("item support2222 ========== try catch additionalObject ${additionalObject}")
				if (additionalObject["id"].asInt != -1 && additionalObject["status"].asInt != -1) {
//                    d("item support2222 ========== ${additionalObject["id"].asInt} ${additionalObject["status"].asInt}")
					if (mViewModel.mListItems.isNotEmpty()) {
//                        d("item support2222 ========== mViewModel.mListItems ${mViewModel.mListItems}")
						var updatePosition = -1
						Completable.fromAction {
							loop@ for (i in 0 until mViewModel.mListItems.size) {
								if (mViewModel.mListItems[i] != null) {
									if (mViewModel.mListItems[i].id == additionalObject["id"].asInt) {
										updatePosition = i
										mViewModel.mListItems[i].status =
											additionalObject["status"].asInt
										mViewModel.mListItems[i].statusText =
											additionalObject["status_text"].asString
										mViewModel.mListItems[i].title =
											additionalObject["title"].asString
//                                        mViewModel.mListItems[i].message = additionalObject["text"].asString
										mViewModel.mListItems[i].cDate =
											additionalObject["c_date"].asString
										mViewModel.mListItems[i].isAdmin =
											additionalObject["is_admin"].asBoolean
										mViewModel.mAdapter?.list?.set(
											i, mViewModel.mListItems[i]
										)
										break@loop
									}
								}
							}
						}.performOnBackOutOnMain(mViewModel.scheduler)
							.doOnComplete {
								val firstItem =
									(binding.rvSupport.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
								val lastItem =
									(binding.rvSupport.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
								if (updatePosition in firstItem..lastItem)
									mViewModel.mAdapter?.notifyItemChanged(updatePosition)
							}.subscribe()
					}
				}
			} catch (e: Exception) {
				d("item support2222 error ===========${e}")
			}
		}
		if (DELETE_NOTIF_KEYS) {
			deleteFromSp(NOTIFICATION_KEY)
			deleteFromSp(NOTIFICATION_ADDITIONAL)
		}

	}


	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as Support
		val arg = extraArguments(item, ARG_STRING_1)
		navTo(R.id.supportDetailFragment, arg = arg)
	}

	override fun onRefresh(refreshLayout: RefreshLayout) {
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.isEmptyView.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		try {
			binding.rvSupport.removeItemDecorationAt(0)
		} catch (ex: Exception) {
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
		mItem as Support
		mViewModel.mListItems.add(0, mItem)
		mViewModel.mAdapter?.list?.add(0, mItem)
		mViewModel.isEmptyView.set(false)
		mViewModel.isShowContent.set(true)
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = SupportAdapter(this.requireContext(), this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (offset == 0 && mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		try {
			binding.rvSupport.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}

		if (!mViewModel.isLoadMore) {
			binding.rvSupport.setHasFixedSize(true)
			binding.rvSupport.layoutManager = LinearLayoutManager(requireContext())
			binding.rvSupport.addItemDecoration(MarginItemDecoration(dp2px(1), marginPosition = 2))
			binding.rvSupport.adapter = mViewModel.mAdapter
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
			object : RequestConnectionResult<MutableList<Support>> {
				override fun onProgress(loading: Boolean) {
					if (loading && mViewModel.mListItems.isEmpty()) {
						mViewModel.isShowContent.set(false)
						binding.rvSupport.showShimmerAdapter()
					} else if (mViewModel.mListItems.isNotEmpty()) {
						binding.refreshLayout.setEnableLoadMore(loading)
					} else {
						binding.rvSupport.hideShimmerAdapter()
					}
				}

				override fun onSuccess(data: MutableList<Support>) {
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
}