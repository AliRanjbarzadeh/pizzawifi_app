package ir.atriatech.extensions.android.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.isEnd(isEndOfRecyclerView: IsEndOfRecyclerView) {
	this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
		override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
			if (dy > 0) {
				val visibleItemCount = layoutManager!!.childCount
				val totalItemCount = layoutManager!!.itemCount
				val pastVisibleItems = (layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()
				if (visibleItemCount + pastVisibleItems >= totalItemCount) {
					isEndOfRecyclerView.onReachToEnd()
				}
			}
		}
	})
}

interface IsEndOfRecyclerView {
	fun onReachToEnd()
}