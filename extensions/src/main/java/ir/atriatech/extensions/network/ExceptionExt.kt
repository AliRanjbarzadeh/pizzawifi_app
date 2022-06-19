package ir.atriatech.extensions.network

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import ir.atriatech.core.DEFAULT_ERROR
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.kotlin.e

fun HttpException.getServerErrorMessage(): Any {
	var errorMessage: Any = DEFAULT_ERROR
	try {
		val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
		val json = response().errorBody()?.string()
		val error = gson.fromJson(json, Msg::class.java)
		e(error.toString())
		if (error.part.isNotEmpty()) {
			errorMessage = error
		} else {
			errorMessage = error.msg
		}

	} catch (ex: Exception) {
		//Network error
		e(ex.message)
	} finally {
		return errorMessage
	}
}

fun Throwable.getDefaultErrorMessage(): String {
	printStackTrace()
	e(message)
	return DEFAULT_ERROR
}
