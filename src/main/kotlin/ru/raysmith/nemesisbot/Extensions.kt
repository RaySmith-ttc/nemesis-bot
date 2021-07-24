package ru.raysmith.nemesisbot

import domain.BooleanResult
import domain.MessageEditResponse
import domain.MessageSendResponse
import ru.raysmith.nemesisbot.tg.Message
import ru.raysmith.nemesisbot.tg.User
import ru.raysmith.nemesisbot.network.ParseMode
import ru.raysmith.nemesisbot.network.TelegramApi
import ru.raysmith.nemesisbot.tg.CallbackQuery
import ru.raysmith.nemesisbot.tg.Chat

fun User.sendMessage(text: String, keyboard: KeyboardMarkup? = null, parseMode: ParseMode? = ParseMode.HTML, disableNotification: Boolean? = null): MessageSendResponse {
    (if (text.length > 4096) text.substring(0, 4093) + "..." else text).let { text ->
        return TelegramApi.service.sendMessage(id, text, keyboardMarkup = keyboard, parseMode = parseMode, disableNotification = disableNotification).execute().body()!!.also {
            lastBotMessageId = it.result.messageId
        }
    }
}

fun User.editMessage(messageId: Int, text: String, keyboard: KeyboardMarkup? = null, parseMode: ParseMode? = ParseMode.HTML): MessageEditResponse {
    (if (text.length > 4096) text.substring(0, 4093) + "..." else text).let { text ->
        return TelegramApi.service.editMessageText(id, messageId, text, replyMarkup = keyboard, parseMode = parseMode).execute().body()!!.also {
            lastBotMessageId = it.result.messageId
        }
    }
}

fun User.editMessage(message: Message, text: String, keyboard: KeyboardMarkup? = null, parseMode: ParseMode? = ParseMode.HTML): MessageEditResponse {
    (if (text.length > 4096) text.substring(0, 4093) + "..." else text).let { text ->
        return editMessage(message.messageId, text, keyboard, parseMode)
    }
}

fun CallbackQuery.answer(text: String? = null, showAlert: Boolean? = null, url: String? = null, cacheTime: Int? = null) {
    TelegramApi.service.answerCallbackQuery(id, text, showAlert, url, cacheTime).execute()
}

fun Chat.deleteMessage(messageId: Int) = TelegramApi.service.deleteMessage(id, messageId).execute()

fun Message.delete(): BooleanResult {
    return TelegramApi.service.deleteMessage(chat.id, messageId).execute().body()!!
}