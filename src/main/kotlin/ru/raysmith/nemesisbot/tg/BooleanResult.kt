package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BooleanResult(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: Boolean
)