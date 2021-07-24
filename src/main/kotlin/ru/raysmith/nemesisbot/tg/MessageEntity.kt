package ru.raysmith.nemesisbot.tg


import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * This object represents one special entity in a text message. For example, hashtags, usernames, URLs, etc.
 *
 * @see <a href="https://core.telegram.org/bots/api#messageentity">api#messageentity</a>
 * */

@Serializable
data class MessageEntity(
    @SerialName("type") val type: MessageEntityType,
    @SerialName("length") val length: Int,
    @SerialName("offset") val offset: Int
)

object MessageEntityTypeSerializer : KSerializer<MessageEntityType> {
    override val descriptor = PrimitiveSerialDescriptor("MessageEntity.type", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MessageEntityType {
        val str = decoder.decodeString().toUpperCase()
        return MessageEntityType.values().find { it.name == str }!!
    }

    override fun serialize(encoder: Encoder, value: MessageEntityType) {
        encoder.encodeString(value.name.toLowerCase())
    }

}

@Serializable(with = MessageEntityTypeSerializer::class)
enum class MessageEntityType {
    MENTION,
    HASHTAG,
    CASHTAG,
    BOT_COMMAND,
    URL,
    EMAIL,
    PHONE_NUMBER,
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH,
    CODE,
    PRE,
    TEXT_LINK,
    TEXT_MENTION
}