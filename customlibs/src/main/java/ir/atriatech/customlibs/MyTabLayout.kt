package ir.atriatech.customlibs

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import io.github.inflationx.calligraphy3.CalligraphyUtils


class MyTabLayout : TabLayout {
	private val mTabs = ArrayList<Tab>()
	private var fontPath: String? = null

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		fontPath = pullFontPathFromView(context, attrs, intArrayOf(R.attr.fontPath))
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun addTab(tab: Tab, setSelected: Boolean) {
		super.addTab(tab, setSelected)

		val mainView = getChildAt(0) as ViewGroup
		val tabView = mainView.getChildAt(tab.position) as ViewGroup
		val tabChildCount = tabView.childCount
		for (i in 0 until tabChildCount) {
			val tabViewChild = tabView.getChildAt(i)
			if (tabViewChild is TextView) {
				CalligraphyUtils.applyFontToTextView(context, tabViewChild, fontPath)
			}
		}
	}

	/**
	 * Tries to pull the Custom Attribute directly from the TextView.
	 *
	 * @param context     Activity Context
	 * @param attrs       View Attributes
	 * @param attributeId if -1 returns null.
	 * @return null if attribute is not defined or added to View
	 */
	fun pullFontPathFromView(context: Context, attrs: AttributeSet?, attributeId: IntArray?): String? {
		if (attributeId == null || attrs == null) return null

		val attributeName: String
		try {
			attributeName = context.resources.getResourceEntryName(attributeId[0])
		} catch (e: Resources.NotFoundException) {
			// invalid attribute ID
			return null
		}

		val stringResourceId = attrs.getAttributeResourceValue(null, attributeName, -1)
		return if (stringResourceId > 0)
			context.getString(stringResourceId)
		else
			attrs.getAttributeValue(null, attributeName)
	}
}