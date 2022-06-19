package ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.titles

import android.content.Context
import android.view.*
import android.widget.LinearLayout
import ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView


class CommonPagerTitleView(context: Context) : LinearLayout(context),
    IMeasurablePagerTitleView {
    var onPagerTitleChangeListener: OnPagerTitleChangeListener? = null
    var contentPositionDataProvider: ContentPositionDataProvider? = null

    override fun onSelected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onSelected(index, totalCount)
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onDeselected(index, totalCount)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun getContentLeft(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentLeft
        } else left
    }

    override fun getContentTop(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentTop
        } else top
    }

    override fun getContentRight(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentRight
        } else right
    }

    override fun getContentBottom(): Int {
        return if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentBottom
        } else bottom
    }


    fun setContentView(contentView: View?, lp: LayoutParams? = null) {
        var lp = lp
        removeAllViews()
        if (contentView != null) {
            if (lp == null) {
                lp = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1f
                )
            }
            lp.gravity = Gravity.CENTER

            addView(contentView, lp)
        }
    }

    fun setContentView(layoutId: Int) {
        val child = LayoutInflater.from(context).inflate(layoutId, null)
        setContentView(child, null)
    }

    interface OnPagerTitleChangeListener {
        fun onSelected(index: Int, totalCount: Int)

        fun onDeselected(index: Int, totalCount: Int)

        fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)

        fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)
    }

    interface ContentPositionDataProvider {
        val contentLeft: Int

        val contentTop: Int

        val contentRight: Int

        val contentBottom: Int
    }
}
/**
 *
 *
 * @param contentView
 */
