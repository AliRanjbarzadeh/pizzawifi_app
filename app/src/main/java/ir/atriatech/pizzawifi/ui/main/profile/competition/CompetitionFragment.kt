package ir.atriatech.pizzawifi.ui.main.profile.competition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentCompetitionBinding
import ir.atriatech.pizzawifi.entities.profile.competition.Competition


class CompetitionFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView,
    OnRefreshListener {
    lateinit var binding: FragmentCompetitionBinding
    private lateinit var mViewModel: CompetitionFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(CompetitionFragmentViewModel::class.java)
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_competition, container, false)

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

        //Check end of data
        checkRvEnd(binding.rvCompetition, this)

        //Refresh listener
        binding.refreshLayout.setOnRefreshListener(this)
    }

    override fun handleNotification() {
        removeUntil(R.id.profileFragment)
    }

    override fun <T> onItemClick(position: Int, view: View, item: T) {
        item as Competition
        val arg = extraArguments(item, ARG_STRING_1)
        navTo(R.id.competitionDetailFragment, arg = arg)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        offset = 0
        mViewModel.isRefreshing = true
        mViewModel.mListItems.clear()
        mViewModel.mAdapter?.list?.clear()
        try {
            binding.rvCompetition.removeItemDecorationAt(0)
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

    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = CompetitionAdapter(this)
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        } else if (offset == 0 && mViewModel.isRefreshing) {
            mViewModel.isRefreshing = false
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        }

        if (!mViewModel.isLoadMore) {
            binding.rvCompetition.setHasFixedSize(true)
            binding.rvCompetition.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCompetition.addItemDecoration(
                MarginItemDecoration(
                    dp2px(2),
                    marginPosition = 2
                )
            )
            binding.rvCompetition.adapter = mViewModel.mAdapter
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

    private fun mObserver() {
        baseListObserver(
            this,
            mViewModel.mObserver,
            object : RequestConnectionResult<MutableList<Competition>> {
                override fun onProgress(loading: Boolean) {
                    if (loading && mViewModel.mListItems.isEmpty()) {
                        mViewModel.isEmptyView.set(false)
                        mViewModel.isShowContent.set(false)
                        binding.rvCompetition.showShimmerAdapter()
                    } else if (mViewModel.mListItems.isNotEmpty()) {
                        binding.refreshLayout.setEnableLoadMore(loading)
                    } else {
                        binding.rvCompetition.hideShimmerAdapter()
                    }
                }

                override fun onSuccess(data: MutableList<Competition>) {
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