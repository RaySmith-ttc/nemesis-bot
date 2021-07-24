package ru.raysmith.nemesisbot.tg


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Error(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error_code") val errorCode: Int,
    @SerialName("description") val description: String
)