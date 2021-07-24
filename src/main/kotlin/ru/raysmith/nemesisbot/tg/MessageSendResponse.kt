package domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.raysmith.nemesisbot.tg.Message

@Serializable
data class MessageSendResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: Message
)

@Serializable
data class MessageEditResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: Message
)

@Serializable
data class MediaSendResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: Message
)