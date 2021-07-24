package ru.raysmith.nemesisbot.tg


import domain.*
import domain.document.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.raysmith.nemesisbot.tg.*

enum class MessageType {
    TEXT,
    COMMAND,
    INLINE_DATA
}

@Serializable
data class Message(
    @SerialName("message_id") val messageId: Int,
    @SerialName("from") val from: User? = null,
    @SerialName("date") val date: Int,
    @SerialName("chat") val chat: Chat,
    @SerialName("entities") val entities: List<MessageEntity>? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("reply_markup") val replyMarkup: ReplyMarkup? = null,
    @SerialName("document") val document: Document? = null,
    @SerialName("animation") val animation: Animation? = null,
    @SerialName("audio") val audio: Audio? = null,
    @SerialName("photo") val photo: List<PhotoSize>? = null,
    @SerialName("video") val video: Video? = null,
    @SerialName("video_note") val videoNote: VideoNote? = null,
    @SerialName("voice") val voice: Voice? = null,
    @SerialName("edit_date") val editDate: Int? = null
) {
    val type = when {
        entities?.first()?.type == MessageEntityType.BOT_COMMAND -> MessageType.COMMAND
        replyMarkup != null -> MessageType.INLINE_DATA
        else -> MessageType.TEXT
    }

    fun hasLink() : Boolean = entities?.find { it.type == MessageEntityType.URL } != null

    fun getLink(): String? = if (hasLink()) text else null
    fun getMedia(): Media? = when {
        document != null -> document
        animation != null -> animation
        audio != null -> audio
        photo != null && photo.isNotEmpty() -> photo.last()
        video != null -> video
        videoNote != null -> videoNote
        voice != null -> voice
        else -> null
    }
}