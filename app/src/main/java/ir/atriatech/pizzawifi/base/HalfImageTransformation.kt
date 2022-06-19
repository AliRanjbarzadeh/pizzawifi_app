package ir.atriatech.pizzawifi.base

import android.graphics.*
import com.squareup.picasso.Transformation

class HalfImageTransformation(private val position: String = "right") : Transformation {
	override fun transform(source: Bitmap): Bitmap {
		val sourceBitmap = Bitmap.createBitmap(source);
//		if (sourceBitmap != source) {
//			source.recycle();
//		}
		val result = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888)
		val canvas = Canvas(result)
		val paint = Paint()
		paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
		canvas.drawBitmap(sourceBitmap, 0f, 0f, null)
		when (position) {
			"right" -> canvas.drawRect(0f, 0f, (sourceBitmap.getWidth() / 2).toFloat(), sourceBitmap.getHeight().toFloat(), paint)

			"left" -> canvas.drawRect((sourceBitmap.getWidth() / 2).toFloat(), 0f, sourceBitmap.getWidth().toFloat(), sourceBitmap.getHeight().toFloat(), paint)
		}
		sourceBitmap.recycle()
		return result
	}

	override fun key(): String {
		return "makerHalf$position"
	}
}