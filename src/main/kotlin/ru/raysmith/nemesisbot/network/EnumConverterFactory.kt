package ru.raysmith.nemesisbot.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import okhttp3.MediaType
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object NetworkUtils {
    class EnumConverterFactory : Converter.Factory() {
        override fun stringConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<*, String>? {
            return if (type is Class<*> && type.isEnum) EnumConverter
            else null
        }

        object EnumConverter : Converter<Enum<*>, String> {
            override fun convert(enum: Enum<*>): String? {
                return when(enum) {
                    is ParseMode -> enum.stringValue
                    is ChatAction -> enum.stringValue
                    else ->  enum.ordinal.toString()
                }
            }
        }
    }

    object AnySerializer : KSerializer<Any> {
        override val descriptor: SerialDescriptor = String.serializer().descriptor

        override fun deserialize(decoder: Decoder): Any {
            require(decoder is JsonDecoder).let {
                val element = decoder.decodeJsonElement()
                return element.toString()
            }
        }

        override fun serialize(encoder: Encoder, value: Any) {
            encoder.encodeString(value.toString())
        }
    }
}

fun String.toMediaType(): MediaType {
    return MediaType.parse(this)!!
}