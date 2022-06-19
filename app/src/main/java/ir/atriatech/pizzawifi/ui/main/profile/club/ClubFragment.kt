package ir.atriatech.pizzawifi.ui.main.profile.club

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.extensions.kotlin.spannableString
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentCustomerClubBinding
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemModel


class ClubFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView, OnRefreshListener {
	lateinit var binding: FragmentCustomerClubBinding
	private lateinit var mViewModel: ClubFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ClubFragmentViewModel::class.java)
		mObserver()
		mUseGiftObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_club, container, false)

		binding.lifecycleOwner = this
//        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
//        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.imgBack.setOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.getData(limit, offset)) {
			setAdapter()
		}

		//Check end of data
		checkRvEnd(binding.rvClubItems, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)

		if (mViewModel.userInfo.image.isNotEmpty()) {
			imageLoader.load(
				mViewModel.userInfo.image.getImageUri(isCustomSize = true, size = "2x"),
				binding.imgClubProfile
			)
		}
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		item as ClubItemModel

		//Dismiss old dialog
		mDialog?.cancel()

		when (view.id) {
			R.id.btnGetItem -> {
				if (AppObject.userInfo.club < item.rate) {
					mDialog = MaterialDialog(requireContext()).apply {
						title(R.string.club_get_rate_error_title)
						message(text = String.format(getString(R.string.club_get_rate_error), item.rate.priceFormat("")))
						positiveButton(R.string._understand)
						show()
					}
				} else {
					val getClubRateText = spannableString(
						firstString = "آیا برای دریافت ",
						secondString = item.name,
						secondColor = R.color.colorAccentSecondary,
						thirdString = " اطمینان دارید؟",
						secondFont = getString(R.string.app_font_bold)
					)
					mDialog = MaterialDialog(requireContext()).apply {
						title(R.string.club_get_rate_sure_title)
						message(text = getClubRateText)
						positiveButton(R.string.yes, click = {
							mViewModel.useGift(item.id)
							it.cancel()
						})
						negativeButton(R.string.close).negativeButton { it.cancel() }
						show()
					}
				}
			}

			R.id.btnShowItem -> {
				mDialog = MaterialDialog(requireContext()).apply {
					title(text = item.name)
					message(text = item.description)
					positiveButton(R.string.close)
					show()
				}
			}
		}
	}


	override fun onRefresh(refreshLayout: RefreshLayout) {
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		try {
			binding.rvClubItems.removeItemDecorationAt(0)
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

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = ClubAdapter(this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (offset == 0 && mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		if (!mViewModel.isLoadMore) {
			binding.rvClubItems.setHasFixedSize(true)
			binding.rvClubItems.layoutManager = LinearLayoutManager(requireContext())
			binding.rvClubItems.addItemDecoration(MarginItemDecoration(dp2px(2), marginPosition = 2))
			binding.rvClubItems.adapter = mViewModel.mAdapter
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
		if (mViewModel.mAdapter != null) {
			handleNotification()
		}
	}

	private fun mObserver() {
		baseListObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<ClubItemModel>> {
				override fun onProgress(loading: Boolean) {
					if (loading && mViewModel.mListItems.isEmpty()) {
						mViewModel.isShowContent.set(false)
						binding.rvClubItems.showShimmerAdapter()
					} else if (mViewModel.mListItems.isNotEmpty()) {
						binding.refreshLayout.setEnableLoadMore(loading)
					} else {
						binding.rvClubItems.hideShimmerAdapter()
					}
				}

				override fun onSuccess(data: MutableList<ClubItemModel>) {
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

	private fun mUseGiftObserver() {
		baseObserver(
			this,
			mViewModel.mUseObserver,
			object : RequestConnectionResult<Msg> {

				override fun onProgress(loading: Boolean) {
					setProgressView(binding.mainView, loading, type = 2)
				}

				override fun onSuccess(data: Msg) {
					mDialog = MaterialDialog(requireContext()).apply {
						title(R.string._success_title)
						message(text = data.msg)
						positiveButton(R.string._understand)
						show()
					}

					AppObject.userInfo.club = data.club
					mViewModel.mAdapter?.notifyDataSetChanged()
				}

				override fun onFailed(errorMessage: String) {
					mDialog = MaterialDialog(requireContext()).apply {
						title(R.string._error_title)
						message(text = errorMessage)
						positiveButton(R.string.close)
						show()
					}
				}
			}
		)
	}
}