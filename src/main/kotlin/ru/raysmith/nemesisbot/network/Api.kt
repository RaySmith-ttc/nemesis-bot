package ru.raysmith.nemesisbot.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import ru.raysmith.utils.PropertiesFactory
import ru.raysmith.nemesisbot.KeyboardMarkup
import ru.raysmith.nemesisbot.ReplyKeyboardMarkup
import java.net.URLDecoder
import java.util.concurrent.TimeUnit
import  ru.raysmith.nemesisbot.tg.Error


class TelegramApiException(private val error: Error) : Exception(error.description) {
    override fun toString(): String {
        return "Code: ${error.errorCode}, Message: ${error.description}"
    }
}

object TelegramApi {

    private val logger = LoggerFactory.getLogger("tg-api")

    private val TOKEN = PropertiesFactory.from("bot.properties").get("token")

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()

            val t1 = System.currentTimeMillis()
            val response = chain.proceed(request)
            val t2 = System.currentTimeMillis()

            val url = response.request().url()
            logger.debug(
                String.format(
                    "Received response for %s in %d ms",
                    URLDecoder.decode(url.toString(), Charsets.UTF_8.toString()).replace("\n", ""), (t2 - t1)
                )
            )

            if (!response.isSuccessful && response.body() != null) {
                val error = json.decodeFromString<Error>(response.body()!!.string())
                throw TelegramApiException(error)
            }

            response
        }
        .build()

    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
        classDiscriminator = "clazz" // resolve `type` field naming conflict
        serializersModule = SerializersModule {
            contextual(NetworkUtils.AnySerializer)
            polymorphic(KeyboardMarkup::class) {
                subclass(ReplyKeyboardMarkup::class, ReplyKeyboardMarkup.serializer())
            }
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.telegram.org/bot$TOKEN/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(NetworkUtils.EnumConverterFactory())
        .build()

    private val fileRetrofit = Retrofit.Builder()
        .baseUrl("https://api.telegram.org/file/bot$TOKEN/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(NetworkUtils.EnumConverterFactory())
        .build()

    val service = retrofit.create(TelegramService::class.java)
    val fileService = fileRetrofit.create(TelegramFileService::class.java)
}