package ir.atriatech.pizzawifi.ui.main.category.product

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
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.checkRvEnd
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentProductBinding
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.ui.main.category.CategoryFragment


class ProductFragment : BaseFragment(), IsEndOfRecyclerView, OnRefreshListener {
    lateinit var binding: FragmentProductBinding
    private lateinit var mViewModel: ProductFragmentViewModel

    companion object {
        fun newInstance(category: Category): ProductFragment {
            val productFragment = ProductFragment()
            val arg = Bundle()
            arg.putParcelable(ARG_STRING_1, category)
            productFragment.arguments = arg

            return productFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(ProductFragmentViewModel::class.java)
        try {
            arguments?.getParcelable<Category>(ARG_STRING_1)?.let {
                mViewModel.category = it
            }
        } catch (e: Exception) {
        }
        d("AppObject.branchShopCart product--> ${AppObject.selectedBranchId}")
        if (AppObject.selectedBranchId != null) {
            mViewModel.branchId = AppObject.selectedBranchId!!
        }
        limit = 100
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = mViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        d("AppObject.branchShopCart product 22 --> ${AppObject.selectedBranchId}")
        if (mViewModel.branchId != null) {
            if (mViewModel.getData(limit, offset)) {
                setAdapter()
            }
        } else {
            eToast(getString(R.string.noBranchIsSpecified))
        }

        //Check end of data
        checkRvEnd(binding.rvProduct, this)

        //Refresh listener
        binding.refreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        offset = 0
        mViewModel.isRefreshing = true
        mViewModel.mListItems.clear()
        mViewModel.mAdapter?.list?.clear()
        try {
            binding.rvProduct.removeItemDecorationAt(0)
        } catch (ex: java.lang.Exception) {
        }

        if (mViewModel.branchId != null) {
            mViewModel.getData(limit, offset)
        } else {
            eToast(getString(R.string.noBranchIsSpecified))
        }


    }

    override fun onReachToEnd() {
        if (!mViewModel.isEnd) {
            mViewModel.isLoadMore = true
            if (mViewModel.branchId != null) {
                mViewModel.getData(limit, offset)
            } else {
                eToast(getString(R.string.noBranchIsSpecified))
            }

        }
    }

    override fun <T> deleteItemFromList(mItem: T, position: Int) {
        if (mItem is Pizza && mViewModel.category.id == 0) {
            if (mItem.shopCount > 0) {
                appDataBase.shopDao().deletePizzaById(mItem.id)
            }
            mViewModel.mListItems.removeAt(position)
            mViewModel.mAdapter?.list?.removeAt(position)
            mViewModel.mAdapter?.notifyItemRemoved(position)
        }
    }

    fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = ProductAdapter(parentFragment as CategoryFragment)
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        } else if (offset == 0 && mViewModel.isRefreshing) {
            mViewModel.isRefreshing = false
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        }

        if (!mViewModel.isLoadMore) {
            binding.rvProduct.setHasFixedSize(true)
            binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())
            binding.rvProduct.addItemDecoration(MarginItemDecoration(dp2px(2), marginPosition = 2))
            binding.rvProduct.adapter = mViewModel.mAdapter
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
            object : RequestConnectionResult<MutableList<Product>> {
                override fun onProgress(loading: Boolean) {
                    if (!mViewModel.isRefreshing && !mViewModel.isLoadMore) {
                        setProgressView(binding.mainView, loading, 2)
                    }
                }

                override fun onSuccess(data: MutableList<Product>) {
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
                    if (mViewModel.branchId != null) {
                        mViewModel.getData(limit, offset)
                    } else {
                        eToast(getString(R.string.noBranchIsSpecified))
                    }


                }
            },
            1
        )
    }
}