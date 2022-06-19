package ir.atriatech.extensions.base.fragment

import androidx.recyclerview.widget.RecyclerView
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.android.ui.isEnd
import ir.atriatech.extensions.dialog.ConnectionCheck
import ir.atriatech.extensions.dialog.connectionCheck

fun BaseCoreFragment.checkRvEnd(recyclerView: RecyclerView, isEndOfRecyclerView: IsEndOfRecyclerView) {
    recyclerView.isEnd(object : IsEndOfRecyclerView {
        override fun onReachToEnd() {
            if (!isAllLoaded && !isLoading) {
                connectionCheck(object : ConnectionCheck {
                    override fun isConnected() {
                        offset += limit
                        isEndOfRecyclerView.onReachToEnd()
                    }
                })
            }
        }
    })
}