package domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.raysmith.nemesisbot.tg.CallbackQuery
import ru.raysmith.nemesisbot.tg.Message
import ru.raysmith.nemesisbot.tg.User

/**
 * This object represents an incoming update.
 * At most one of the optional parameters can be present in any given update
 *
 * @see <a href="https://core.telegram.org/bots/api#update">api#update</a>
 * */
@Serializable
data class Update(
    @SerialName("update_id") val updateId: Long,
    @SerialName("message") val message: Message? = null,
    @SerialName("edited_message") val editedMessage: Message? = null,
    @SerialName("inline_query") val inlineQuery: InlineQuery? = null,
    @SerialName("callback_query") val callbackQuery: CallbackQuery? = null,
)

/**
 * This object represents an incoming inline query. When the user sends an empty query, your bot could return some default or trending results.
 *
 * @param location Optional. Sender location, only for bots that request user location
 * @param query Text of the query (up to 256 characters)
 * @param offset Offset of the results to be returned, can be controlled by the bot
 *
 * @see <a href="https://core.telegram.org/bots/api#inlinequery">api#inlinequery</a>
 * */
@Serializable
data class InlineQuery(
    @SerialName("id") val id: String,
    @SerialName("from") val from: User,
    @SerialName("location") val location: Location? = null,
    @SerialName("query") val query: String,
    @SerialName("offset") val offset: String,
)

/**
 * This object represents a point on the map.
 *
 * @see <a href="https://core.telegram.org/bots/api#location">api#location</a>
 * */
@Serializable
data class Location(
    @SerialName("longitude") val longitude: Float,
    @SerialName("latitude") val latitude: Float
)