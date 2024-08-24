package com.cuscus.uotsap2
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.lang.reflect.Type

interface ApiService {
    @POST("login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("search_user")
    suspend fun searchUser(@Body searchRequest: SearchRequest): Response<SearchResponse>

    @POST("sendMessage") // Assicurati che l'endpoint corrisponda a quello del tuo server
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<Unit>

    @POST("get_messages")
    suspend fun getMessages(@Body request: GetMessagesRequest): Response<GetMessagesResponse>
}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val message: String)

data class SearchRequest(val username: String)
data class SearchResponse(val message: String)

data class GetMessagesRequest(val username: String, val chatUsername: String)
data class GetMessagesResponse(val messages: List<Message>)

data class SendMessageRequest(
    val id: String,
    val sender: String,
    val recipient: String,
    val message: String
)


data class Message(
    val id: String, // Aggiungi questa proprietà
    val sender: String,
    val recipient: String,
    val message: String,
    val timestamp: String,
    val status: String
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun getFormattedTimestamp(timestamp: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            return LocalDateTime.parse(timestamp, formatter)
        }
    }
}

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(
        src: LocalDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.format(formatter))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime {
        return LocalDateTime.parse(json.asString, formatter)
    }
}

// Retrofit setup
@RequiresApi(Build.VERSION_CODES.O)
val gson = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
    .create()

@RequiresApi(Build.VERSION_CODES.O)
val retrofit = Retrofit.Builder()
    .baseUrl("http://192.168.1.212:5000/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

@RequiresApi(Build.VERSION_CODES.O)
val apiService: ApiService = retrofit.create(ApiService::class.java)




