package com.cuscus.uotsap2

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID


object Destinations {
    const val Chats = "chats"
    const val Troll = "troll"
    const val Settings = "settings"
    const val S_Account = "account"
    const val Welcome = "welcome"
    const val Login = "login"
    const val Register = "register"
    const val Rage = "rage"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Prime (navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("mqtt_credentials", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", null)

    LaunchedEffect(username) {
        if (username == null) {
            navController.navigate(Destinations.Welcome) {
                popUpTo(Destinations.Chats) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController as NavHostController,
        startDestination = if (username != null) Destinations.Chats else Destinations.Welcome
    ) {
        composable(Destinations.Chats) { Chats(navController) }
        composable(Destinations.Troll) { Troll(navController) }
        composable(Destinations.Settings) { Settings(navController) }
        composable(Destinations.Welcome) { WelcomeScreen(navController) }
        composable(Destinations.Login) { LoginScreen(navController) }
        composable(Destinations.Register) { RegisterScreen(navController) }
        composable(Destinations.Rage) { Rage(navController) }
        composable(
            "chat/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatUsername = backStackEntry.arguments?.getString("username")
            ChatScreen(navController, chatUsername ?: "")
        }
    }

    val state = rememberScrollState()

    val chatIcon = painterResource(id = R.drawable.round_chat_24)
    val settingsIcon = painterResource(id = R.drawable.round_settings_24)
    val trollIcon = painterResource(id = R.drawable.round_lunch_dining_24)
    val accountIcon = painterResource(id = R.drawable.round_manage_accounts_24)

    val items = listOf("Chats", "?", "Settings")
    val icons = listOf(
        chatIcon,
        trollIcon,
        settingsIcon
    )

    Column {
        // Main content of the screen
        Spacer(modifier = Modifier.weight(1f))

        NavigationBar(
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 4.dp
        ) {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route

            items.forEachIndexed { index, item ->
                val isSelected = currentRoute == Destinations.Chats && index == 0 ||
                        currentRoute == Destinations.Troll && index == 1 ||
                        currentRoute == Destinations.Settings && index == 2 // Added for AnimationNavhostScreen

                NavigationBarItem(
                    modifier = Modifier,
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledIconColor = MaterialTheme.colorScheme.scrim,
                        disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    icon = { Icon(painter = icons[index], contentDescription = item) },
                    label = { Text(item) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            val destination = when (index) {
                                0 -> Destinations.Chats
                                1 -> Destinations.Troll
                                2 -> Destinations.Settings
                                // Add other cases for additional items
                                else -> null
                            }

                            // Check if the destination is different from the current one
                            if (currentRoute != destination) {
                                if (destination != null) {
                                    navController.navigate(destination)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Chats(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("mqtt_credentials", Context.MODE_PRIVATE)
    val savedUsername = sharedPreferences.getString("username", null)

    // Effettua il controllo non appena si accede alla schermata Chats
    LaunchedEffect(savedUsername) {
        if (savedUsername == null) {
            // Se l'utente non è loggato, reindirizzalo alla schermata di login
            navController.navigate(Destinations.Welcome) {
                popUpTo(Destinations.Chats) { inclusive = true }
            }
        }
    }

    if (savedUsername != null) {
        // Se l'utente è loggato, mostra la schermata delle chat
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Nome utente in alto
            Text(
                text = stringResource(id = R.string.LoggedInAs, savedUsername),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo di ricerca e bottone per iniziare una nuova chat
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cerca Utente") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedButton(
                onClick = {
                    scope.launch {
                        try {
                            val response = apiService.searchUser(SearchRequest(searchQuery))
                            if (response.isSuccessful) {
                                navController.navigate("chat/$searchQuery")
                            } else {
                                errorMessage = "Utente non trovato"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Errore di rete: ${e.localizedMessage}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Inizia Chat")
            }
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(navController: NavController, chatUsername: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val messages = remember { MutableStateFlow<List<Message>>(emptyList()) }
    var currentMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    // Registra il BroadcastReceiver per ascoltare i nuovi messaggi
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val messageId = intent?.getStringExtra("messageId")
                val messageBody = intent?.getStringExtra("messageBody")
                val messageSender = intent?.getStringExtra("messageSender")
                val recipient = intent?.getStringExtra("chatUsername")

                if (messageId != null && messageBody != null && messageSender != null && recipient == chatUsername) {
                    // Aggiungi il messaggio alla lista solo se il destinatario corrisponde alla chat corrente
                    val newMessage = Message(
                        id = messageId,
                        sender = messageSender,
                        recipient = recipient,
                        message = messageBody,
                        timestamp = LocalDateTime.now(ZoneOffset.UTC).toString(),
                        status = "received"
                    )
                    messages.value = messages.value + newMessage
                    scrollToBottom(scope, scrollState, messages.value.size)
                }
            }
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(
            receiver,
            IntentFilter("NEW_MESSAGE_ACTION")
        )

        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    // Interfaccia utente della chat
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 60.dp)
    ) {
        val currentMessages by messages.collectAsState()

        LazyColumn(
            modifier = Modifier.weight(1f),
            state = scrollState
        ) {
            items(currentMessages) { message ->
                Text(text = "${message.sender}: ${message.message}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                label = { Text("Scrivi un messaggio...") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (currentMessage.isNotEmpty()) {
                        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
                        val newMessage = Message(
                            id = UUID.randomUUID().toString(),
                            sender = "LocalUser",  // Sostituisci con l'utente attuale
                            recipient = chatUsername,
                            message = currentMessage,
                            timestamp = currentDateTime.toString(),
                            status = "sent"
                        )

                        scope.launch {
                            val success = sendMessageToServer(newMessage) // Usa la funzione appena definita

                            if (success) {
                                messages.value = messages.value + newMessage
                                currentMessage = ""
                                scrollToBottom(scope, scrollState, messages.value.size)
                            } else {
                                Log.d("ChatScreen", "Invio del messaggio fallito")
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Invia messaggio"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun sendMessageToServer(message: Message): Boolean {
    val request = SendMessageRequest(
        id = message.id,
        sender = message.sender,
        recipient = message.recipient,
        message = message.message
    )

    return try {
        val response = apiService.sendMessage(request)
        response.isSuccessful
    } catch (e: Exception) {
        Log.e("sendMessageToServer", "Errore nell'invio del messaggio", e)
        false
    }
}



//@RequiresApi(Build.VERSION_CODES.O)
//suspend fun fetchMessages(
//    recipient: String,
//    chatUsername: String,
//    onNewMessages: (List<Message>) -> Unit
//) {
//    val request = GetMessagesRequest(username = recipient, chatUsername = chatUsername)
//    val response = apiService.getMessages(request)
//
//    if (response.isSuccessful) {
//        val serverMessages = response.body()?.messages?.map { serverMessage ->
//            // Se l'ID è nullo, genera un ID temporaneo unico
//            if (serverMessage.id == null) {
//                serverMessage.copy(id = UUID.randomUUID().toString())
//            } else {
//                serverMessage
//            }
//        } ?: emptyList()
//
//        // Log per vedere quali messaggi vengono ricevuti dal server
//        Log.d("fetchMessages", "Messaggi ricevuti dal server (tutti): ${serverMessages.size}")
//
//        // Temporaneamente non filtriamo i messaggi per verificare se vengono ricevuti correttamente
//        // Valutare la condizione di filtro se necessario
//        val receivedMessages = serverMessages // .filter { it.status == "received" }
//
//        Log.d("fetchMessages", "Messaggi da aggiungere dopo il filtro: ${receivedMessages.size}")
//
//        onNewMessages(receivedMessages)
//    } else {
//        Log.d("fetchMessages", "Errore nel recupero dei messaggi: ${response.errorBody()}")
//        // Gestisci l'errore di caricamento dei messaggi
//    }
//}

//@RequiresApi(Build.VERSION_CODES.O)
//suspend fun sendMessage(
//    id: String,        // Add id parameter
//    sender: String,
//    recipient: String,
//    message: String
//): Boolean {
//    val request = SendMessageRequest(
//        id = id,         // Include the ID in the request
//        sender = sender,
//        recipient = recipient,
//        message = message
//    )
//    val response = apiService.sendMessage(request)
//    return response.isSuccessful
//}

// Funzione per scorrere automaticamente all'ultimo messaggio
private fun scrollToBottom(scope: CoroutineScope, scrollState: LazyListState, messageCount: Int) {
    scope.launch {
        scrollState.animateScrollToItem(messageCount - 1)
    }
}

fun sendNotification(context: Context, title: String, message: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("chat_messages", "Chat Messages", importance)
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "chat_messages")
        .setSmallIcon(R.drawable.round_chat_24)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
}



@Composable
fun Troll (navController: NavController, modifier: Modifier = Modifier) {
    val state = rememberScrollState()

}

@Composable
fun Settings (navController: NavController, modifier: Modifier = Modifier) {
    val state = rememberScrollState()

}


@Composable
fun WelcomeScreen (navController: NavController, modifier: Modifier = Modifier) {
    val state = rememberScrollState()
    val context = LocalContext.current

    val image: Painter = painterResource(id = R.drawable.whatsappdue)
    val image2: Painter = painterResource(id = R.drawable.baseline_no_accounts_24)

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Image(painter = image,contentDescription = "", modifier = Modifier
            .size(200.dp)
            .padding(30.dp))

        Text(text = stringResource(id = R.string.WelcomeBig), fontSize = 25.sp)

        Divider(
            modifier = Modifier.padding(0.dp, 30.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )



        Text(text = stringResource(id = R.string.WelcomeScript), fontSize = 18.sp, textAlign = TextAlign.Center, lineHeight = 30.sp)

        Row (
            modifier
                .padding(20.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ElevatedButton(
                onClick = {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (!notificationManager.areNotificationsEnabled()) {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                1
                            )
                        }
                    }

                    navController.navigate(Destinations.Register) {
                        popUpTo(Destinations.Chats) { inclusive = true }
                    }
                },
                modifier = Modifier,
                enabled = true,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(),
                contentPadding = PaddingValues(horizontal = 30.dp, vertical = 12.dp)
            ) {
                Text(text = stringResource(id = R.string.WelcomeButtonYes), fontSize = 20.sp)
            }

            ElevatedButton(
                onClick = {
                    navController.navigate(Destinations.Rage) {
                        popUpTo(Destinations.Chats) { inclusive = true }
                    }
                },
                modifier = Modifier,
                enabled = true,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(),
                contentPadding = PaddingValues(horizontal = 30.dp, vertical = 12.dp)
            ) {
                Text(text = stringResource(id = R.string.WelcomeButtonNo), fontSize = 20.sp)
            }
        }



        TextButton(
            onClick = {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (!notificationManager.areNotificationsEnabled()) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                    }
                }

                navController.navigate(Destinations.Login) {
                    popUpTo(Destinations.Chats) { inclusive = true }
                }
            },
            modifier = Modifier,
            enabled = true,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.textButtonColors(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(stringResource(id = R.string.WelcomeButtonAlready), fontSize = 15.sp)
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val errorMessageTranslation = stringResource(id = R.string.ErrorAut)

    val image = painterResource(id = R.drawable.round_account_circle_24)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = image, contentDescription = "", modifier = Modifier.size(150.dp), colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.Login), fontSize = 30.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.Username)) },
            enabled = true,
            readOnly = false,
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            enabled = true,
            readOnly = false,
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedButton(
            onClick = {
                scope.launch {
                    try {
                        val response = apiService.loginUser(LoginRequest(username, password))
                        if (response.isSuccessful) {
                            val message = response.body()?.message

                            // Salva le credenziali se il login ha successo
                            val sharedPref = navController.context.getSharedPreferences("mqtt_credentials", Context.MODE_PRIVATE)
                            sharedPref.edit().apply {
                                putString("username", username)
                                putString("password", password)
                                apply()
                            }
                            navController.navigate(Destinations.Chats)
                        } else {
                            errorMessage = errorMessageTranslation
                        }
                    } catch (e: Exception) {
                        errorMessage = "Errore di rete: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier,
            enabled = true,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(),
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.Login), fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                navController.navigate(Destinations.Register)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.DontHaveAnAccount_SignUp))
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var buttonPressed by remember { mutableStateOf(false) }
    var textError by remember { mutableStateOf("") }
    var isRequestInProgress by remember { mutableStateOf(false) }
    var checkedStatePasswordVisible = remember { mutableStateOf(false) }

    // Stato per verificare se il server ha risposto
    var serverResponded by remember { serverResponded }
    val flaskErrorMessage by remember { FlaskErrorMessage }
    val flaskUsernameTaken by remember { flaskErrorUsernameTaken }
    val passwordFlaskIsFineState by remember { passwordFlaskIsFine }
    val comeOnState by remember { comeOn }
    val textErrorScript =  stringResource(id = R.string.AlreadyExistentUsername)

    val image = painterResource(id = R.drawable.round_manage_accounts_24)

    LaunchedEffect(serverResponded) {
        if (serverResponded) {
            when {
                comeOnState -> {
                    textError = "Registrazione riuscita!"
                    isRequestInProgress = false

                    // Reset degli stati
                    comeOn.value = false
                    serverResponded = false

                    // Navigazione alla schermata di Login
                    navController.navigate(Destinations.Login) {
                        popUpTo(Destinations.Register) { inclusive = true }
                    }
                }
                flaskUsernameTaken -> {
                    textError = textErrorScript
                    isRequestInProgress = false
                }
                !passwordFlaskIsFineState -> {
                    textError = flaskErrorMessage
                    isRequestInProgress = false
                }
            }
            buttonPressed = false // Resetta il flag di pressione del bottone
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = "",
            modifier = Modifier.size(150.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.Register), fontSize = 30.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.Username)) },
            enabled = true,
            readOnly = false,
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            enabled = true,
            readOnly = false,
            textStyle = MaterialTheme.typography.bodyMedium,
            supportingText = {
                Text(text = stringResource(id = R.string.NoSharePassword))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (checkedStatePasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = {
                Text(
                    text = stringResource(id = R.string.ConfirmPassword),
                    color = if (password != confirmPassword) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    }
                )
            },
            enabled = true,
            readOnly = false,
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = if (password != confirmPassword) {
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.error,
                    unfocusedBorderColor = MaterialTheme.colorScheme.errorContainer
                )
            } else {
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                )
            }
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {

            Checkbox(
                checked = checkedStatePasswordVisible.value,
                onCheckedChange = { checkedStatePasswordVisible.value = it },
                enabled = true,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    uncheckedColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkmarkColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledCheckedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledUncheckedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledIndeterminateColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
            Spacer(modifier = Modifier.width(0.dp))
            TextButton(
                onClick = {
                    checkedStatePasswordVisible.value = !checkedStatePasswordVisible.value
                }
            ) {
                Text(
                    text = stringResource(id = R.string.ShowPassword),
                    modifier = Modifier.padding(0.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }

        if (buttonPressed) {
            when {
                password != confirmPassword -> {
                    textError = stringResource(id = R.string.ConfirmPasswordError)
                }
                username.isEmpty() || password.isEmpty() -> {
                    textError = stringResource(id = R.string.BadCharactersError)
                }
                !isRequestInProgress -> {
                    textError = ""

                    isRequestInProgress = true

                    // Invia la richiesta al server
                    registerUser(username, password, navController)
                }
            }
        }

        Text(text = textError)

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                buttonPressed = true
            },
            modifier = Modifier,
            enabled = !isRequestInProgress, // Disabilita il bottone mentre la richiesta è in corso
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(),
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.Register), fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                navController.navigate(Destinations.Login)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.AlreadyHaveAnAccount_Login))
        }
    }
}

@Composable
fun Rage (navController: NavController, modifier: Modifier = Modifier) {
    val state = rememberScrollState()
    val context = LocalContext.current

    val image: Painter = painterResource(id = R.drawable.baseline_child_care_24)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = image, contentDescription = "", modifier = Modifier
                .size(200.dp)
                .padding(30.dp)
        )

        Text(
            text = stringResource(id = R.string.RageSpeech),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(Modifier.requiredHeight(15.dp))

        ElevatedButton(
            onClick = {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (!notificationManager.areNotificationsEnabled()) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                    }
                }

                navController.navigate(Destinations.Register) {
                    popUpTo(Destinations.Chats) { inclusive = true }
                }
            },
            modifier = Modifier.padding(16.dp),
            enabled = true,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(),
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.ISubmit), fontSize = 20.sp)
        }

        var progress by remember { mutableStateOf(0.1f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "Progress animation"
        )


        LinearProgressIndicator(
            modifier = Modifier
                .semantics(mergeDescendants = true) {}
                .padding(10.dp),
            progress = animatedProgress,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
        )

        var i = 0
        LaunchedEffect(Unit) {
            while(i < 10) {
                if (progress < 1f) progress += 0.09f
                delay(1000)
                i++

                if (i == 10) {
                    val x = 9/0
                }
            }
        }
    }
}





