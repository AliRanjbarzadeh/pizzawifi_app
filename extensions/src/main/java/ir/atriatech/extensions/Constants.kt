package ir.atriatech.extensions

import org.json.*

/**
 * connection
 */
const val BAD_REQUEST = 400
const val UNAUTHORIZED = 403

const val DEFAULT_LANGUAGE = ir.atriatech.core.BuildConfig.DEFAULT_LANGUAGE

fun isValidJson(json: String): Boolean {
	try {
		JSONObject(json)
		return true
	} catch (ex: JSONException) {
		try {
			JSONArray(json)
			return true
		} catch (ex: JSONException) {
		}
	}
	return false
}