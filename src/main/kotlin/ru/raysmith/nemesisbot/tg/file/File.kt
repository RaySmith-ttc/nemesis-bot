package domain.file


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class File(
    @SerialName("file_id") val id: String,
    @SerialName("file_path") val path: String,
    @SerialName("file_size") val size: Int,
    @SerialName("file_unique_id") val uniqueId: String
)