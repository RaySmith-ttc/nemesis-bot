package ru.raysmith.nemesisbot.tg


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("username") val username: String? = null
) {
//    fun getFullNameWithNick(): String = "$firstName $lastName ($username)".let { s -> s.take(256) }
}