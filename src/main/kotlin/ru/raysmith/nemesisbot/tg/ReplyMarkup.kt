package domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.raysmith.nemesisbot.InlineKeyboard

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard") val inlineKeyboard: List<List<InlineKeyboard>>
)