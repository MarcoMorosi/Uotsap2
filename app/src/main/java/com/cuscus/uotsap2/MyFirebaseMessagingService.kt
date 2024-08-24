package com.cuscus.uotsap2

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Verifica se il messaggio contiene dati
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Dati ricevuti: ${remoteMessage.data}")

            val messageId = remoteMessage.data["messageId"]
            val messageBody = remoteMessage.data["message"]
            val messageSender = remoteMessage.data["sender"]
            val chatUsername = remoteMessage.data["recipient"] // Assicurati di aggiungere 'recipient' ai dati FCM

            if (messageId != null && messageBody != null && messageSender != null && chatUsername != null) {
                // Esegui l'aggiornamento della UI o della logica dell'app con il nuovo messaggio
                handleNewMessage(messageId, messageBody, messageSender, chatUsername)
            }
        }

        // Verifica se il messaggio contiene una notifica
        remoteMessage.notification?.let {
            Log.d("FCM", "Titolo Notifica: ${it.title}")
            Log.d("FCM", "Corpo Notifica: ${it.body}")
        }
    }

    private fun handleNewMessage(messageId: String, messageBody: String, messageSender: String, chatUsername: String) {
        // Logica per gestire e visualizzare il messaggio
        // Aggiorna l'interfaccia utente o notifica la composizione della chat
        val intent = Intent("NEW_MESSAGE_ACTION")
        intent.putExtra("messageId", messageId)
        intent.putExtra("messageBody", messageBody)
        intent.putExtra("messageSender", messageSender)
        intent.putExtra("chatUsername", chatUsername)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
