package ru.raysmith.nemesisbot.network

import domain.*
import domain.file.FileResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.raysmith.nemesisbot.KeyboardMarkup
import ru.raysmith.nemesisbot.tg.UpdatesResult
import java.lang.Exception

object ParseModeSerializer : KSerializer<ParseMode> {
    override val descriptor = PrimitiveSerialDescriptor("ParseMode", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = decoder.decodeString().let { mode ->
        when(mode) {
            "MarkdownV2" -> ParseMode.MARKDOWN
            "HTML" -> ParseMode.HTML
            else -> throw Exception("Unknown parse mode: $mode")
        }
    }

    override fun serialize(encoder: Encoder, value: ParseMode) {
        encoder.encodeString(value.stringValue)
    }

}

@Serializable(with = ParseModeSerializer::class)
enum class ParseMode(val stringValue: String) {
    HTML("HTML"),
    MARKDOWN("MarkdownV2")
}

interface TelegramFileService {
    @GET("{file_path}")
    fun downLoad(
        @Path("file_path") filePath: String
    ): Call<ResponseBody>
}

interface InputMedia

/**
 * Represents a photo to be sent.
 *
 * @param type 	Type of the result, must be photo
 * @param media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://<file_attach_name>” to upload a new one using multipart/form-data under <file_attach_name> name.
 * @param caption Optional. Caption of the photo to be sent, 0-1024 characters after entities parsing
 * @param parse_mode Optional. Mode for parsing entities in the photo caption.
 *
 * @see <a href="https://core.telegram.org/bots/api#inputmediaphoto">api#inputmediaphoto</a>
 * @see <a href="https://core.telegram.org/bots/api#sending-files">More info on Sending Files »</a>
 * */
@Serializable
data class InputMediaPhoto(
    @SerialName("type") val type: String,
    @SerialName("media") val media: String,
    @SerialName("caption") val caption: String? = null,
    @SerialName("parse_mode") val parseMode: String? = null
) : InputMedia

interface TelegramService {

    @GET("getUpdates")
    fun getUpdates(
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Int? = null,
        @Query("timeout") timeout: Int = 60,
        @Query("allowed_updates") allowedUpdates: String = "[\"callback_query\",\"message\"]"
    ): Call<UpdatesResult>

    @GET("sendMessage")
    fun sendMessage(
        @Query("chat_id") chatId: Int,
        @Query("text") text: String,
        @Query("parse_mode") parseMode: ParseMode? = null,
        @Query("disable_web_page_preview") disableWebPagePreview: Boolean? = null,
        @Query("disable_notification") disableNotification: Boolean? = null,
        @Query("reply_to_message_id") replyToMessageId: Int? = null,
        @Query("reply_markup") keyboardMarkup: KeyboardMarkup? = null
    ): Call<MessageSendResponse>

    @GET("sendPhoto")
    fun sendPhoto(
        @Query("chat_id") chatId: Int,
        @Query("photo") photo: String,
        @Query("caption") caption: String? = null,
        @Query("parse_mode") parseMode: ParseMode? = null,
        @Query("disable_notification") disableNotification: Boolean? = null,
        @Query("reply_to_message_id") replyToMessageId: Int? = null,
        @Query("allow_sending_without_reply") allowSendingWithoutReply: Boolean? = null,
        @Query("reply_markup") keyboardMarkup: KeyboardMarkup? = null
    ): Call<MessageSendResponse>

    @GET("sendDocument")
    fun sendDocument(
        @Query("chat_id") chatId: Int,
        @Query("document") document: String,
    ): Call<MediaSendResponse>

    @GET("editMessageText")
    fun editMessageText(
        @Query("chat_id") chatId: Int,
        @Query("message_id") messageId: Int? = null,
        @Query("text") text: String,
        @Query("reply_markup") replyMarkup: KeyboardMarkup? = null,
        @Query("inline_message_id") inlineMessageId: Int? = null,
        @Query("parse_mode") parseMode: ParseMode? = null,
        @Query("disable_web_page_preview") disableWebPagePreview: Boolean? = null
    ): Call<MessageEditResponse>

    @GET("editMessageCaption")
    fun editMessageCaption(
        @Query("chat_id") chatId: Int,
        @Query("message_id") messageId: Int? = null,
        @Query("caption") caption: String,
        @Query("reply_markup") replyMarkup: KeyboardMarkup? = null,
        @Query("inline_message_id") inlineMessageId: Int? = null,
        @Query("parse_mode") parseMode: ParseMode? = null
    ): Call<MessageEditResponse>

    @GET("editMessageMedia")
    fun editMessageMedia(
        @Query("chat_id") chatId: Int,
        @Query("message_id") messageId: Int? = null,
        @Query("media") media: InputMedia,
        @Query("reply_markup") replyMarkup: KeyboardMarkup? = null,
        @Query("inline_message_id") inlineMessageId: Int? = null
    ): Call<MessageEditResponse>

    @GET("editMessageReplyMarkup")
    fun editMessageReplyMarkup(
        @Query("chat_id") chatId: Int,
        @Query("message_id") messageId: Int? = null,
        @Query("inline_message_id") inlineMessageId: Int? = null,
        @Query("reply_markup") replyMarkup: KeyboardMarkup? = null,
    ): Call<MessageEditResponse>

    @GET("deleteMessage")
    fun deleteMessage(
        @Query("chat_id") chatId: Int,
        @Query("message_id") messageId: Int
    ): Call<BooleanResult>

    @GET("answerCallbackQuery")
    fun answerCallbackQuery(
        @Query("callback_query_id") callbackQueryId: String,
        @Query("text") text: String? = null,
        @Query("show_alert") showAlert: Boolean? = null,
        @Query("url") url: String? = null,
        @Query("cache_time") cacheTime: Int? = null,
    ): Call<BooleanResult>

    @GET("sendChatAction")
    fun sendChatAction(
        @Query("chat_id") chatId: Int,
        @Query("action") action: ChatAction
    ): Call<BooleanResult>

    @GET("getFile")
    fun getFile(
        @Query("file_id") fileId: String,
    ): Call<FileResponse>

}

enum class ChatAction(val stringValue: String) {
    TYPING("typing"),
    UPLOAD_PHOTO("upload_photo"),
    RECORD_VIDEO("record_video"),
    UPLOAD_VIDEO("upload_video"),
    RECORD_VOICE("record_voice"),
    UPLOAD_VOICE("upload_voice"),
    UPLOAD_DOCUMENT("upload_document"),
    FIND_LOCATION("find_location"),
    RECORD_VIDEO_NOTE("record_video_note"),
    UPLOAD_VIDEO_NOTE("upload_video_note"),
}