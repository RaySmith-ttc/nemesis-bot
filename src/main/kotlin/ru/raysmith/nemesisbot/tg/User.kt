package ru.raysmith.nemesisbot.tg


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.raysmith.nemesisbot.Goal
import ru.raysmith.nemesisbot.Location

@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("is_bot") val isBot: Boolean,
    @SerialName("language_code") val languageCode: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("username") val username: String? = null,
    @Transient var location: Location = Location.START,
    @Transient var role: UserRole = UserRole.PLAYER,
    @Transient var name: String = "Unknown",
    @Transient var lastBotMessageId: Int? = null,
    @Transient var lastBotRefreshMessageId: Int? = null,
    @Transient var isReady: Boolean = false,
    @Transient var isInGame: Boolean = true,
    @Transient var goals: List<Goal> = listOf(),
    @Transient var selectedGoal: Goal? = null
) {

    override fun toString(): String {
        return """
            ${role.name.lowercase().replaceFirstChar { it.uppercaseChar() }}: #$id [$name], location=$location, lastBotMessageId=$lastBotMessageId, isReady=$isReady, isInGame=$isInGame, goals=${goals.joinToString { it.title }}, selectedGoal=${selectedGoal?.title}
        """.trimIndent()
    }

}

enum class UserRole {
    ADMIN, PLAYER
}