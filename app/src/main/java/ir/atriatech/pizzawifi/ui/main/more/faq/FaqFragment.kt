package ir.atriatech.pizzawifi.ui.main.more.faq

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
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentFaqBinding
import ir.atriatech.pizzawifi.entities.more.faq.Faq


class FaqFragment : BaseFragment(), OnRefreshListener {
    lateinit var binding: FragmentFaqBinding
    private lateinit var mViewModel: FaqFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(FaqFragmentViewModel::class.java)
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_faq, container, false)

        binding.lifecycleOwner = this
//		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
//		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.imgBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mViewModel.getData()) {
            setAdapter()
        }

        //Refresh listener
        binding.refreshLayout.setOnRefreshListener(this)
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel.isRefreshing = true
        mViewModel.isShowContent.set(false)
        mViewModel.mListItems.clear()
        mViewModel.mAdapter?.list?.clear()
        binding.rvFaq.removeAllViews()
        mViewModel.getData()
    }

    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = FaqAdapter(object : RecyclerViewTools {
                override fun <T> onItemClick(position: Int, view: View, item: T) {
                    item as Faq
                    item.isExpanded = !item.isExpanded
                    mViewModel.mAdapter!!.notifyItemChanged(position)
                }
            })
            d("mViewModel.mAdapter.list -- > ${mViewModel.mListItems.size}")

            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        } else if (mViewModel.isRefreshing) {
            mViewModel.isRefreshing = false
            d("mViewModel.mAdapter.list refreshing -- > ${mViewModel.mListItems.size}")
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        }

        try {
            binding.rvFaq.removeItemDecorationAt(0)
        } catch (e: Exception) {
        }

        binding.rvFaq.setHasFixedSize(true)
        binding.rvFaq.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFaq.addItemDecoration(MarginItemDecoration(dp2px(2), MarginItemDecoration.TOP))
        binding.rvFaq.adapter = mViewModel.mAdapter
    }

    private fun mObserver() {
        baseObserver(
            this,
            mViewModel.mObserver,
            object : RequestConnectionResult<MutableList<Faq>> {
                override fun onProgress(loading: Boolean) {
                    if (loading) {
                        binding.rvFaq.showShimmerAdapter()
                    } else {
                        binding.rvFaq.hideShimmerAdapter()
                    }
                }

                override fun onSuccess(data: MutableList<Faq>) {
                    binding.refreshLayout.finishRefresh()
                    mViewModel.mListItems = data
                    mViewModel.isEmptyView.set(data.size == 0)
                    setAdapter()
                }

                override fun onFailed(errorMessage: String) {
                    mViewModel.getData()
                }
            },
            4
        )
    }
}