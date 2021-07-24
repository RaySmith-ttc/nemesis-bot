package ru.raysmith.nemesisbot

import ru.raysmith.nemesisbot.tg.Message

enum class BotCommand(val value: String, val description: String, val adminOnly: Boolean) {
    START("/start", "Начать новую игру", false),
//    RESET("/reset", "Отменить все регистрации", true),
    PLAYERS("/players", "Показать всех игроков", false),
    UNKNOWN("", "", false);

    companion object {
        fun getByMessage(message: Message) =
            values().find { it.value.isNotEmpty() && (it.value == message.text) } ?: UNKNOWN
    }
}