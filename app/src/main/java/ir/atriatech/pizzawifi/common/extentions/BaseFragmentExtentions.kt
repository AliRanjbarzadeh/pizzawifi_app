package ir.atriatech.pizzawifi.common.extentions

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.util.others.FakeAdapter

fun BaseFragment.setUpFakeAdapter(
    @LayoutRes item_layout: Int, rvFake: RecyclerView,
    size: Int = 5,
    smartRefreshLayout: SmartRefreshLayout? = null,
    shouldUseShimmer: Boolean = true,
    transitionName: String = "",
    @IdRes transitionId: Int? = null,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext()),
    recyclerViewItemClick: RecyclerViewTools
): RecyclerView.Adapter<*>? {
    try {
        val fakeAdapter = FakeAdapter(
            item_layout,
            size,
            transitionName = transitionName,
            transitionId = transitionId,
            recyclerViewItemClick = object : RecyclerViewTools {
                override fun <T> onItemClick(position: Int, view: View, t: T) {
                    recyclerViewItemClick.onItemClick(position, view, t)
                }
            })
        rvFake.layoutManager = layoutManager
        rvFake.adapter = fakeAdapter
//		if (shouldUseShimmer) {
//			if (rvFake is ShimmerRecyclerViewX) {
//				rvFake.showShimmerAdapter()
//				rvFake.postDelayed({
//					rvFake.hideShimmerAdapter()
//					smartRefreshLayout?.finishRefresh()
//				}, 1200)
//			}
//		}

        smartRefreshLayout?.setOnRefreshListener {
            //			if (rvFake is ShimmerRecyclerViewX) {
//				rvFake.showShimmerAdapter()
//				rvFake.postDelayed({
//					rvFake.hideShimmerAdapter()
//					smartRefreshLayout.finishRefresh()
//				}, 1200)
//			} else {
//				smartRefreshLayout.postDelayed({
//					smartRefreshLayout.finishRefresh()
//				}, 1200)
//			}
            smartRefreshLayout.postDelayed({
                smartRefreshLayout.finishRefresh()
            }, 1200)
        }
    } catch (e: Exception) {
    }
    return rvFake.adapter
}