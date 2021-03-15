package com.kst.testingnotification


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.greenrobot.eventbus.EventBus


class MyMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("fcm", "refresh token:$token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.run {
            var msg = StringBuffer()

            notification?.let {

                val message = "channel id = ${it.channelId} msg title:${it.title}, \nbody:${it.body}"
                Log.e(
                    "fcm_notification",
                    message
                )
                msg = msg.append(message)
            }
            data.forEach {
                val m = "key:${it.key}, value:${it.value}"
                Log.e("fcm_foreach", "m:$it")
                msg.append(m)
            }
            sendNotification(msg.toString())
            EventBus.getDefault().post(msg.toString())
        }

    }

    private fun sendNotification(msg: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
            .setContentTitle("FCM Message")
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}


