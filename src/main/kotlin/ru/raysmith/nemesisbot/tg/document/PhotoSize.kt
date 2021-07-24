package domain.document


import ru.raysmith.nemesisbot.tg.Media
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoSize(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("file_size") override val fileSize: Int? = null,
    override val fileName: String? = null
): Media