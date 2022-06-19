package ir.atriatech.extensions.network

import android.net.Uri
import androidx.annotation.NonNull
import ir.atriatech.extensions.kotlin.attachString
import okhttp3.*
import java.io.File


@NonNull
fun createPartFromString(descriptionString: String): RequestBody {
	return RequestBody.create(
		okhttp3.MultipartBody.FORM, descriptionString
	)
}

fun createPartFromUri(uri: Uri, fileName: String, fileType: String = "image/*"): MultipartBody.Part {
	val file = File(uri.path)
	val requestFile: RequestBody = RequestBody.create(
		MediaType.parse(fileType),
		file
	)
	return MultipartBody.Part.createFormData(fileName, file.name, requestFile)

}

fun createListPartFromUri(uriList: MutableList<Uri>? = null, fileName: String, fileType: String = "image/*"): MutableList<MultipartBody.Part>? {
	return if (uriList != null && uriList.isNotEmpty()) {
		val multiPartList = mutableListOf<MultipartBody.Part>()
		var counter = 0
		for (i in uriList) {
			val file = File(i.path)
			val requestFile: RequestBody = RequestBody.create(
				MediaType.parse(fileType),
				file
			)
			multiPartList.add(MultipartBody.Part.createFormData(attachString(false, fileName, "_", counter.toString()), file.name, requestFile))
			counter += 1
		}
		multiPartList
	} else {
		null
	}

}


