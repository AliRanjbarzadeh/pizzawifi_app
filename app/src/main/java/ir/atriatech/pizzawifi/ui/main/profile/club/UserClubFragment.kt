package ir.atriatech.pizzawifi.ui.main.profile.club

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentProfileClubBinding
import ir.atriatech.pizzawifi.databinding.ItemClubProfileDetailBinding
import ir.atriatech.pizzawifi.databinding.ItemClubProfileDetailGiftValueBinding
import ir.atriatech.pizzawifi.entities.profile.club.ClubItemProfileModel


class UserClubFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView, OnRefreshListener {
	lateinit var binding: FragmentProfileClubBinding
	private lateinit var mViewModel: UserClubFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(UserClubFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_club, container, false)

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
		item as ClubItemProfileModel

		//Dismiss old dialog
		mDialog?.cancel()

		when (view.id) {
			R.id.btnShowItem -> {
				val itemClubProfileDetailBinding = DataBindingUtil.inflate<ItemClubProfileDetailBinding>(LayoutInflater.from(requireContext()), R.layout.item_club_profile_detail, null, false)
				itemClubProfileDetailBinding.item = item

				val packageName = requireContext().packageName

				item.giftValue?.let { json ->
					val keys = json.keySet()

					keys.forEach { jsonKey ->
						val itemClubProfileDetailGiftValueBinding = DataBindingUtil.inflate<ItemClubProfileDetailGiftValueBinding>(LayoutInflater.from(requireContext()), R.layout.item_club_profile_detail_gift_value, null, false)
						try {
							val resId = resources.getIdentifier("club_$jsonKey", "string", packageName)
							itemClubProfileDetailGiftValueBinding.item = getString(resId) + " : " + json.get(jsonKey).asString
						} catch (e: Exception) {
							itemClubProfileDetailGiftValueBinding.item = jsonKey + " : " + json.get(jsonKey).asString
						} finally {
							itemClubProfileDetailGiftValueBinding.executePendingBindings()
						}

						itemClubProfileDetailBinding.llGiftValue.addView(itemClubProfileDetailGiftValueBinding.root)
					}
				}
				itemClubProfileDetailBinding.executePendingBindings()

				mDialog = MaterialDialog(requireContext(), BottomSheet()).apply {
					title(text = item.name)
					message(text = item.description)
					customView(view = itemClubProfileDetailBinding.root)
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
			mViewModel.mAdapter = UserClubAdapter(this)
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
			object : RequestConnectionResult<MutableList<ClubItemProfileModel>> {
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

				override fun onSuccess(data: MutableList<ClubItemProfileModel>) {
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