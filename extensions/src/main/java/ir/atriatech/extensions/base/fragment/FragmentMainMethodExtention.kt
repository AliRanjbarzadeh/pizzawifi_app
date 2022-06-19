package ir.atriatech.extensions.base.fragment

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikelau.views.shimmer.ShimmerRecyclerViewX
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import ir.atriatech.core.base.BaseCoreAdapter
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.core.base.MainFragmentCallBack
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView

fun <T> BaseCoreFragment.setParameters(
    backButton: AppCompatTextView? = null,
    recycler: RecyclerView? = null,
    isFixSize: Boolean? = false,
    adapter: BaseCoreAdapter<T>? = null,
    adapterList: MutableList<T> = mutableListOf(),
    progressViewParent: ViewGroup? = null,
    emptyViewParent: ViewGroup? = null,
    keyboardViewParent: ViewGroup? = progressViewParent,
    refreshLayout: SmartRefreshLayout? = null,
    useLoadMore: Boolean = false,
    useRefreshLayout: Boolean = false,
    useShimmer: Boolean = false,
    layoutManager: RecyclerView.LayoutManager? = LinearLayoutManager(requireContext()),
    mainFragmentCallBack: MainFragmentCallBack? = null
) {
    smartRefreshLayout = refreshLayout
    backButton?.setOnClickListener {
        back()
    }
    recycler?.let { rv ->
        adapter?.let { mAdapter ->
            rv.layoutManager = layoutManager
            rv.adapter = mAdapter
            isFixSize?.let {
                rv.setHasFixedSize(it)
            }
            recyclerView = rv
        }
    }
    keyboardViewParent?.let {
        hideKeyboard(it)
    }
    emptyViewParent?.let {
        emptyVP = it

    }
    progressViewParent?.let {
        progressVP = it
    }
    smartRefreshLayout?.let {
        it.setOnRefreshListener {
            offset = 0
            adapter?.list?.clear()
            mainFragmentCallBack?.requestToNet()
            mainFragmentCallBack?.onRefreshListener()
        }
    }
    if (useLoadMore) {
        recycler?.let {
            checkRvEnd(it, object : IsEndOfRecyclerView {
                override fun onReachToEnd() {
                    mainFragmentCallBack?.requestToNet()
                    mainFragmentCallBack?.onReachToEndListener()

                }

            })
        }
    }
    if (adapterList.isEmpty()) {
        if (recycler is ShimmerRecyclerViewX) {
            recycler.showShimmerAdapter()
        }
        mainFragmentCallBack?.requestToNet()
    } else {
        adapter?.list?.clear()
        adapter?.addToList(adapterList)
    }
}