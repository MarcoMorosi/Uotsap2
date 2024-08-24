package com.cuscus.uotsap2

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class ChatNotificationService (
    private val context: Context,

) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    fun showBasicNotification(Title: String, Text: String, Icon: Int, Priority: Int, AutoCancel: Boolean) {
        val notification = NotificationCompat.Builder(context, "chat_messages")
            .setContentTitle(Title)
            .setContentText(Text)
            .setSmallIcon(Icon)
            .setPriority(Priority)
            .setAutoCancel(AutoCancel)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showExpandableNotification() {
        val image = context.bitmapFromResource(R.drawable.whatsappdue)

        val notification = NotificationCompat.Builder(context, "chat_messages")
            .setContentTitle("Chat Notification")
            .setContentText("Ecco il messaggio: BLA BLA BLA")
            .setSmallIcon(R.drawable.round_chat_24)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setLargeIcon(image)
            .setStyle(
                NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null as Bitmap?)
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )

    }

    private fun Context.bitmapFromResource(
        @DrawableRes resId: Int
    ) = BitmapFactory.decodeResource(
        resources,
        resId
    )
}