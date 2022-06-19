package ir.atriatech.core

import android.os.Build
import java.util.*


const val DEFAULT_ERROR = "خطا در برقراری ارتباط"
const val LOCATION_PROVIDER_GPS_RESULT = 1000
const val ARG_STRING_1 = "ARG_STRING_1"
const val ARG_STRING_2 = "ARG_STRING_2"
const val ARG_STRING_3 = "ARG_STRING_3"
const val ARG_STRING_4 = "ARG_STRING_4"
const val ARG_PARCELABLE_1= "ARG_PARCELABLE_1"
const val ARG_INT_1= "ARG_INT_1"
const val LANGUAGE_PERSIAN = "fa"
const val LANGUAGE_ENGLISH = "en"
const val MY_SERIAL = "Serial"


/**
 * Return pseudo unique ID
 * @return ID
 */
fun getUniquePsuedoID(): String {
    val m_szDevIDShort =
        "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10

    var serial: String?
    try {
        serial = android.os.Build::class.java.getField("SERIAL").get(null)!!.toString()

        // Go ahead and return the serial for api => 9
        return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    } catch (exception: Exception) {
        // String needs to be initialized
        serial = "serial" // some value
    }

    return UUID(m_szDevIDShort.hashCode().toLong(), serial!!.hashCode().toLong()).toString()
}

fun generateRandomString(len: Int = 15): String {
    val alphanumerics = CharArray(26) { it -> (it + 97).toChar() }.toSet()
        .union(CharArray(9) { it -> (it + 48).toChar() }.toSet())
    return (0..len - 1).map {
        alphanumerics.toList().random()
    }.joinToString("")
}