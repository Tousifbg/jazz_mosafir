package pk.mosafir.travsol.firebase

import android.annotation.SuppressLint
import android.app.Notification.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pk.mosafir.travsol.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.google.firebase.analytics.FirebaseAnalytics

import android.os.Bundle
import pk.mosafir.travsol.R
import pk.mosafir.travsol.utils.toast


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        val TAG = "token"
    }
    @SuppressLint("LogNotTimber")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    @SuppressLint("LogNotTimber")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "Notification: " + remoteMessage.from)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            Log.d(TAG, "Notification Title: $title - Body: $body")

            if (title != null) {
                if (body != null) {
                    showNotification(title, body)
                }
            }
        }

        if (remoteMessage.data.size > 0) {

            for ((key, value) in remoteMessage.data) {
                Log.d(TAG, "Key: $key - Value: $value")
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            "channel_id", "Test Notification Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.description = "My test notification channel"
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(title: String, body: String) {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)

        val builder = NotificationCompat.Builder(
            applicationContext, "channel_id"
        )
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        notificationManager.notify(1, builder.build())
    }

}