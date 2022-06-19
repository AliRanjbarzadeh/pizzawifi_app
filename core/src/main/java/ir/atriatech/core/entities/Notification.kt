package ir.atriatech.core.entities

/**
 * Created by victor on 01/04/16.
 */
class Notification(val title: String, val body: String) {
    val timeStamp: Long = System.currentTimeMillis()

}
