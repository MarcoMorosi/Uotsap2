//package com.cuscus.whatsapp2
//
//import android.Manifest
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import android.util.Log
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import org.eclipse.paho.android.service.MqttAndroidClient
//import org.eclipse.paho.client.mqttv3.IMqttActionListener
//import org.eclipse.paho.client.mqttv3.IMqttToken
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions
//import org.eclipse.paho.client.mqttv3.MqttException
//import org.eclipse.paho.client.mqttv3.MqttMessage
//
//class MqttManager(private val context: Context, clientId: String) {
//    private val mqttAndroidClient: MqttAndroidClient
//
//    init {
//        val serverUri = "tcp://192.168.1.212:1883" // Sostituisci con il tuo broker MQTT
//        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
//        createNotificationChannel() // Creazione del canale di notifica durante l'inizializzazione
//    }
//
//    fun connect(onConnected: () -> Unit, onConnectionFailed: (Exception) -> Unit) {
//        try {
//            val options = MqttConnectOptions().apply {
//                isAutomaticReconnect = true
//                isCleanSession = true
//            }
//
//            mqttAndroidClient.connect(options, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    println("Connesso al broker MQTT")
//                    onConnected() // Chiama il callback di connessione riuscita
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    println("Connessione al broker MQTT fallita: ${exception.message}")
//                    onConnectionFailed(exception as Exception)
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//            onConnectionFailed(e)
//        }
//    }
//
//    fun publish(topic: String, message: String) {
//        // Verifica se il client è connesso prima di pubblicare
//        if (mqttAndroidClient.isConnected) {
//            try {
//                val mqttMessage = MqttMessage(message.toByteArray())
//                mqttAndroidClient.publish(topic, mqttMessage)
//                println("Messaggio pubblicato su $topic")
//            } catch (e: MqttException) {
//                e.printStackTrace()
//                println("Errore nella pubblicazione del messaggio: ${e.message}")
//            }
//        } else {
//            println("Client MQTT non connesso, impossibile inviare il messaggio")
//        }
//    }
//
//    fun subscribe(topic: String, onMessageReceived: (String) -> Unit) {
//        if (mqttAndroidClient.isConnected) {
//            try {
//                mqttAndroidClient.subscribe(topic, 0, null, object : IMqttActionListener {
//                    override fun onSuccess(asyncActionToken: IMqttToken) {
//                        println("Sottoscritto al topic: $topic")
//                    }
//
//                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                        println("Errore nella sottoscrizione al topic: $topic - ${exception.message}")
//                    }
//                }) { _, message ->
//                    onMessageReceived(message.toString())
//
//                    // Invio la notifica al recipient
//                    val notificationService = ChatNotificationService(context)
//                    notificationService.showBasicNotification(
//                        "Nuovo messaggio da $topic",
//                        message.toString(),
//                        R.drawable.whatsappdue,
//                        NotificationManager.IMPORTANCE_HIGH,
//                        true
//                    )
//                }
//            } catch (e: MqttException) {
//                e.printStackTrace()
//                println("Errore nella sottoscrizione al topic: ${e.message}")
//            }
//        } else {
//            println("Client MQTT non connesso, impossibile iscriversi al topic")
//        }
//    }
//
//    fun connectAndSubscribe(topic: String, onMessageReceived: (String) -> Unit) {
//        connect(onConnected = {
//            subscribe(topic, onMessageReceived) // Sottoscrizione dopo la connessione
//        }, onConnectionFailed = { exception ->
//            println("Connessione fallita: ${exception.message}")
//        })
//    }
//
//    private fun sendNotification(title: String, message: String) {
//        Log.d("Notification", "Invio notifica con titolo: $title e messaggio: $message")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                Log.d("Notification", "Permesso per notifiche non concesso")
//                return
//            }
//        }
//
//        val notificationId = (System.currentTimeMillis() % 10000).toInt()
//
//        val notification = NotificationCompat.Builder(context, "chat_messages")
//            .setSmallIcon(R.drawable.round_chat_24)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//            .build()
//
//        NotificationManagerCompat.from(context).notify(notificationId, notification)
//        Log.d("Notification", "Notifica inviata con ID: $notificationId")
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Chat Messages"
//            val descriptionText = "Notifications for new chat messages"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel("chat_messages", name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager: NotificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    fun disconnect() {
//        if (mqttAndroidClient.isConnected) {
//            try {
//                mqttAndroidClient.disconnect()
//                println("Disconnesso dal broker MQTT")
//            } catch (e: MqttException) {
//                e.printStackTrace()
//                println("Errore nella disconnessione: ${e.message}")
//            }
//        } else {
//            println("Client MQTT non connesso")
//        }
//    }
//}
