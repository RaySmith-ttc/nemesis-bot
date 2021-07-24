package ru.raysmith.nemesisbot


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data") val callbackData: String = "",
    @SerialName("text") val text: String
)