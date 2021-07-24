package ru.raysmith.nemesisbot.tg

import domain.document.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Media {
    @SerialName("file_id") val fileId: String
    @SerialName("file_unique_id") val fileUniqueId: String
    @SerialName("file_size") val fileSize: Int?
    @SerialName("file_name") val fileName: String?
}

@Serializable
data class Document(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("thumb") val thumb: PhotoSize? = null,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null
) : Media

@Serializable
data class Animation(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("thumb") val thumb: PhotoSize? = null,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("duration") val duration: Int,
) : Media

@Serializable
data class Audio(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("thumb") val thumb: PhotoSize? = null,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("performer") val performer: String? = null,
    @SerialName("title") val title: String? = null,
) : Media

@Serializable
data class Video(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("thumb") val thumb: PhotoSize? = null,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("duration") val duration: Int,
) : Media

@Serializable
data class VideoNote(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("thumb") val thumb: PhotoSize? = null,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null,
    @SerialName("length") val length: Int,
    @SerialName("duration") val duration: Int,
) : Media

@Serializable
data class Voice(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null,
    @SerialName("duration") val duration: Int,
) : Media

@Serializable
data class Contact(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val fileUniqueId: String,
    @SerialName("file_name") override val fileName: String? = null,
    @SerialName("file_size") override val fileSize: Int? = null,
    @SerialName("duration") val duration: Int,
) : Media