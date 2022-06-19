package ir.atriatech.extensions.android

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager


val displayMetrics = ir.atriatech.extensions.app.app.resources.displayMetrics!!

inline val screenWidth: Int
	get() = ir.atriatech.extensions.app.app.resources.displayMetrics.widthPixels

inline val screenHeight: Int
	get() = ir.atriatech.extensions.app.app.resources.displayMetrics.heightPixels

inline val screenDensity: Float
	get() = ir.atriatech.extensions.app.app.resources.displayMetrics.density

inline val scaledDensity: Float
	get() = ir.atriatech.extensions.app.app.resources.displayMetrics.scaledDensity

fun getStatusBarHeight(): Int {
	var result = 0
	val resourceId = ir.atriatech.extensions.app.app.resources.getIdentifier("status_bar_height", "dimen", "android")
	if (resourceId > 0) {
		result = ir.atriatech.extensions.app.app.resources.getDimensionPixelSize(resourceId)
	}
	return result
}

fun getVirNavBarHeight(): Int {
	var height: Int
	val wm = (ir.atriatech.extensions.app.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
	val display = wm.defaultDisplay
	val p = Point()
	display.getSize(p)
	val screenHeight = Math.max(p.y, p.x)
	val dm = DisplayMetrics()
	val c: Class<*> = Class.forName("android.view.Display")
	val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
	method.invoke(display, dm)
	//横屏在右|竖屏在底
	height = Math.max(dm.heightPixels, dm.widthPixels) - screenHeight
	//横屏在底|竖屏在底
	if (height == 0) {
		height = Math.min(dm.heightPixels, dm.widthPixels) - Math.min(p.y, p.x)
	}
	return height
}

//ReplaceWith()目前不支持将方法转换字段，所以需要手动修改
fun dp2px(dp: Number) = (dp.toFloat() * displayMetrics.density + 0.5f).toInt()

fun sp2px(sp: Number) = (sp.toFloat() * displayMetrics.scaledDensity + 0.5f).toInt()

fun px2dp(px: Number) = (px.toFloat() / displayMetrics.density + 0.5f).toInt()

fun px2sp(px: Number) = (px.toFloat() / displayMetrics.scaledDensity + 0.5f).toInt()
