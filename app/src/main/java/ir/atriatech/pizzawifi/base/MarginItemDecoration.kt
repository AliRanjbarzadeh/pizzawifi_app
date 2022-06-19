package ir.atriatech.pizzawifi.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val mHeight: Int,
    private val marginPosition: Int = 1,
    private val isShowOnFirstItem: Boolean = false
) : RecyclerView.ItemDecoration() {
    companion object {
        val BOTTOM = 1
        val TOP = 2
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            when (marginPosition) {
                1 -> {//BOTTOM
                    if (isShowOnFirstItem) {
                        bottom = mHeight
                    } else if (parent.getChildAdapterPosition(view) != 0) {
                        bottom = mHeight
                    }

                }
                2 -> {//TOP
                    if (isShowOnFirstItem) {
                        top = mHeight
                    } else if (parent.getChildAdapterPosition(view) != 0) {
                        top = mHeight
                    }
                }
            }
        }
    }

}