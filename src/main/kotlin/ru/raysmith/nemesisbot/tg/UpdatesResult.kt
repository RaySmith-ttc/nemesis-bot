package ru.raysmith.nemesisbot.tg

import domain.Update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatesResult(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: List<Update>
)