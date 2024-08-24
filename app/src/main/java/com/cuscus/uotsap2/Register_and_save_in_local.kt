package com.cuscus.uotsap2

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// Il nome parla da sè
fun areCredentialsSaved(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("mqtt_credentials", Context.MODE_PRIVATE)
    val username = sharedPref.getString("username", null)
    val password = sharedPref.getString("password", null)
    return username != null && password != null
}

// Registrazione
var passwordFlaskIsFine = mutableStateOf(false)
var FlaskErrorMessage = mutableStateOf("")
var flaskErrorUsernameTaken = mutableStateOf(false)
var serverResponded = mutableStateOf(false)
var comeOn = mutableStateOf(false)

fun registerUser(username: String, password: String, navController: NavController) {
    Log.d("RegisterUser", "Inizio registrazione utente: $username")

    val client = OkHttpClient()
    val json = """
        {
            "username": "$username",
            "password": "$password"
        }
    """
    Log.d("RegisterUser", "JSON da inviare: $json")

    val body = json.toRequestBody("application/json".toMediaTypeOrNull())
    val request = Request.Builder()
        .url("http://192.168.1.212:5000/register")
        .post(body)
        .build()

    Log.d("RegisterUser", "Invio richiesta al server Flask")

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("RegisterUser", "Errore durante la registrazione: ${e.message}")
            passwordFlaskIsFine.value = false
            FlaskErrorMessage.value = "${e.message}"
            serverResponded.value = true // Aggiorna lo stato esistente
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful) {
                Log.d("RegisterUser", "Registrazione riuscita, salvando credenziali")
                passwordFlaskIsFine.value = true

                val sharedPref = navController.context.getSharedPreferences("mqtt_credentials", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("username", username)
                    putString("password", password)
                    apply()
                }
                serverResponded.value = true // Aggiorna lo stato esistente
                comeOn.value = true
            } else {
                Log.e("RegisterUser", "Registrazione fallita, codice di risposta: ${response.code}")
                passwordFlaskIsFine.value = false
                if (response.code == 400) {
                    val errorMessage = JSONObject(responseBody).getString("error")
                    flaskErrorUsernameTaken.value = true
                    FlaskErrorMessage.value = "${response.code}"
                } else {
                    FlaskErrorMessage.value = "Errore sconosciuto"
                }
                serverResponded.value = true // Aggiorna lo stato esistente
            }
        }
    })
}


