package ir.atriatech.pizzawifi.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.BuildConfig.LOGIN_SESSION_KEY
import ir.atriatech.core.BuildConfig.PUSH_TOKEN_SESSION_KEY
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.existInSp
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.saveToSp
import ir.atriatech.extensions.app.topActivity
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.common.dagger.AppDH
import ir.atriatech.pizzawifi.repository.MainRepository
import ir.atriatech.pizzawifi.ui.intro.IntroActivity
import ir.atriatech.pizzawifi.ui.main.MainActivity
import me.leolin.shortcutbadger.ShortcutBadger
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MyMessageService : FirebaseMessagingService() {

    private val bag = CompositeDisposable()
    private val component by lazy { AppDH.baseComponent() }

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var repository: MainRepository

    init {
        component.inject(this)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNullOrEmpty())
            return

        if (!loadFromSp<Boolean>(LOGIN_SESSION_KEY, false)) {
            return
        }
        try {
            val noticeId = createID()
            var badgeCount: Int = 0
            if (existInSp(NOTIFICATION_BADGE_COUNT)) {
                badgeCount = loadFromSp(NOTIFICATION_BADGE_COUNT, 0)
            }
            var newBadge = badgeCount + 1


            when (remoteMessage.data[NOTIFICATION_KEY]) {
                "peyk" -> {
                    val broadCastIntent = Intent(NOTIFICATION_BROADCAST)
                    broadCastIntent.putExtra(NOTIFICATION_KEY, remoteMessage.data[NOTIFICATION_KEY])
                    broadCastIntent.putExtra(
                        NOTIFICATION_ADDITIONAL,
                        gson.toJson(remoteMessage.data)
                    )
                    sendBroadcast(broadCastIntent)
                    return
                }
            }

            //region Click notification
            val resultIntent: Intent = if (topActivity is MainActivity) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, IntroActivity::class.java)
            }

            resultIntent.action = NOTIFICATION_BROADCAST

            val pendingIntent: PendingIntent

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
            resultIntent.putExtra(NOTIFICATION_KEY, remoteMessage.data[NOTIFICATION_KEY])
            resultIntent.putExtra(
                NOTIFICATION_ADDITIONAL,
                remoteMessage.data[NOTIFICATION_ADDITIONAL]
            )
            resultIntent.putExtra(NOTIFICATION_BADGE_COUNT, newBadge)

            pendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            //endregion

            //region Delete Notification
//            val deletePendingIntent: PendingIntent
//            val deleteIntent = Intent(this, MyNotificationReceiver::class.java)
//            deleteIntent.action = NOTIFICATION_BROADCAST
//            deleteIntent.putExtra(MyAppObjects.Notification.notificationId, noticeId)
//
//            deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            //endregion

            val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

            //region Big text style
            val noticeStyle = NotificationCompat.BigTextStyle()
                .bigText(remoteMessage.data["msg"])
                .setBigContentTitle(remoteMessage.data["title"])

            when (remoteMessage.data[NOTIFICATION_KEY]) {
                "orders" -> {
                    try {
                        val broadCastIntent = Intent(NOTIFICATION_BROADCAST)
                        broadCastIntent.putExtra(
                            NOTIFICATION_KEY,
                            remoteMessage.data[NOTIFICATION_KEY]
                        )
                        broadCastIntent.putExtra(
                            NOTIFICATION_ADDITIONAL,
                            remoteMessage.data[NOTIFICATION_ADDITIONAL]
                        )
                        sendBroadcast(broadCastIntent)

                        val additional = gson.fromJson<Msg>(
                            remoteMessage.data[NOTIFICATION_ADDITIONAL],
                            Msg::class.java
                        )
                        if (additional.statusText.isNotEmpty()) {
                            noticeStyle.setSummaryText(additional.statusText)
                        }
                    } catch (ex: Exception) {
                        e(ex.message, "onMessageReceived")
                    }
                }
                "support", "message", "wallet" -> {
                    try {
                        val broadCastIntent = Intent(NOTIFICATION_BROADCAST)
                        broadCastIntent.putExtra(
                            NOTIFICATION_KEY,
                            remoteMessage.data[NOTIFICATION_KEY]
                        )
                        broadCastIntent.putExtra(
                            NOTIFICATION_ADDITIONAL,
                            remoteMessage.data[NOTIFICATION_ADDITIONAL]
                        )
                        sendBroadcast(broadCastIntent)

                    } catch (ex: Exception) {
                        e(ex.message, "onMessageReceived")
                    }
                }
            }
            //endregion

            //region Builder
            val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.data["title"]) //Title small notice on app icon touch hold and collapsed notice
                .setContentText(remoteMessage.data["msg"]) //Content small notice on app icon touch hold
                .setColor(ContextCompat.getColor(this, R.color.black))
                .setStyle(noticeStyle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
//                .setDeleteIntent(deletePendingIntent)
                .setAutoCancel(true)

            ShortcutBadger.applyNotification(
                applicationContext,
                mBuilder.build(),
                newBadge
            ) // use only for Xiaomi
            ShortcutBadger.applyCount(
                this,
                newBadge
            ) //for 1.1.4+  use for other_ lower than api 26(android 8)

            saveToSp(NOTIFICATION_BADGE_COUNT, newBadge)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(mBuilder.build(), noticeId)
            } else {
                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(noticeId, mBuilder.build())
            }
            //endregion
        } catch (ex: Exception) {
            repository.saveLogError("${ex.message} => " + gson.toJson(ex.stackTrace), bag)
        }
    }

    override fun onNewToken(pushToken: String) {
        super.onNewToken(pushToken)
        saveToSp(PUSH_TOKEN_SESSION_KEY, pushToken)
        repository.savePushToken(pushToken, bag)
    }

    //region Create channel
    private fun createNotificationChannel(notification: Notification, noticeId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Defaults"
            val description = "Orders, Supports, Messages"
            val importance = NotificationManager.IMPORTANCE_HIGH

            //Notification Manager
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            try {
                var channel = notificationManager.getNotificationChannel(CHANNEL_ID)
                if (channel == null) {
                    channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                        setShowBadge(true)
                    }
                    //Channel
//                    channel = NotificationChannel(CHANNEL_ID, name, importance)
                    channel.description = description
                    channel.enableVibration(true)
                    channel.enableLights(true)
                    channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(noticeId, notification)
            } catch (ex: Exception) {
            }
        }
    }
    //endregion

    //region Create notification id
    fun createID(): Int {
        val now = Date()
        return Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now))
    }
    //endregion
}