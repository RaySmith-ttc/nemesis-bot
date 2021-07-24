package ru.raysmith.nemesisbot.tg


import ru.raysmith.nemesisbot.tg.From
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.raysmith.nemesisbot.tg.Message

@Serializable
data class CallbackQuery(
    @SerialName("chat_instance") val chatInstance: String,
    @SerialName("data") val `data`: String,
    @SerialName("from") val from: From,
    @SerialName("id") val id: String,
    @SerialName("message") val message: Message
)