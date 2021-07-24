package ru.raysmith.nemesisbot.tg


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class From(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("id")
    val id: Int,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("username")
    val username: String? = null
)