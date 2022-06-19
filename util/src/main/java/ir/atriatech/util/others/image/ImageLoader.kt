package ir.atriatech.util.others.image

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.util.R
import java.io.File
import javax.inject.Inject


class ImageLoader @Inject constructor(private val picasso: Picasso) {


	fun load(@Nullable uri: Uri?, imageView: ImageView, isCenterCrop: Boolean = true) {
		uri?.let { mUri ->
			picasso.load(mUri).apply {
				error(R.drawable.img_place_holder)
				if (isCenterCrop) {
					fit()
					centerCrop()
				}
				into(imageView)
			}
		}

	}

	fun load(@Nullable uri: Uri?, imageView: ImageView, isCenterCrop: Boolean = true, @DrawableRes errorDrawable: Int = R.drawable.img_place_holder) {
		uri?.let { mUri ->
			picasso.load(mUri).apply {
				error(errorDrawable)
				if (isCenterCrop) {
					fit()
					centerCrop()
				}
				into(imageView, object : Callback {
					override fun onSuccess() {
						d("image loaded successfully $mUri", "TabItem")
					}

					override fun onError(ex: Exception?) {
						e("${ex?.message} $mUri", "TabItem")
					}

				})
			}
		}

	}

	fun loadFitCenter(@Nullable uri: Uri?, imageView: ImageView) {
		uri?.let { mUri ->
			picasso.load(mUri).apply {
				error(R.drawable.img_place_holder)
				into(imageView)
			}
		}
	}

	fun loadFitCenter(@DrawableRes resourceId: Int, imageView: ImageView) {
		picasso.load(resourceId)
			.error(R.drawable.img_place_holder)
			.into(imageView)
	}

	fun loadWithTransform(
		@Nullable uri: Uri?,
		imageView: ImageView,
		transformation: Transformation,
		isCenterCrop: Boolean = true
	) {
		uri?.let { mUri ->
			picasso.load(mUri).apply {
				error(R.drawable.img_place_holder)
				transform(transformation)
				if (isCenterCrop) {
					fit()
					centerCrop()
				}
				into(imageView)
			}
		}
	}

	fun loadWithCallBack(
		@Nullable uri: Uri,
		imageView: ImageView,
		isCenterCrop: Boolean = true,
		callback: Callback
	) {
		picasso.load(uri).apply {
			error(R.drawable.img_place_holder)
			if (isCenterCrop) {
				fit()
				centerCrop()
			}
			into(imageView, object : Callback {
				override fun onSuccess() {
					callback.onSuccess()
				}

				override fun onError(e: Exception?) {
					callback.onError(e)
				}

			})
		}
	}

	fun load(file: File, imageView: ImageView) {
		picasso.load(file)
//            .placeholder(R.drawable.gif1)
			.into(imageView)
	}

	fun load(@DrawableRes resourceId: Int, imageView: ImageView) {
		picasso.load(resourceId)
			.error(R.drawable.img_place_holder)

			.fit()
			.centerCrop()
			.noFade()

			.into(imageView)
	}

	fun load(@Nullable path: String, imageView: ImageView) {
		picasso.load(path)
//            .placeholder(R.drawable.gif1)

			.into(imageView)
	}


}