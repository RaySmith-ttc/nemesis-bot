package ru.raysmith.nemesisbot

import domain.Update
import ru.raysmith.nemesisbot.tg.Message
import ru.raysmith.nemesisbot.network.ParseMode
import ru.raysmith.nemesisbot.tg.User
import ru.raysmith.utils.endWordForNumWithNumber
import kotlin.time.ExperimentalTime

interface EventHandler {
    val user: User
    val message: Message
    fun handle()

//    fun sendMainMenu(message: String = "", prefixMessage: String = "", isEdit: Boolean = true) {
//        val text = if (message.isEmpty())
//            "${prefixMessage}\nВыберите пункт меню"
//        else "${prefixMessage}\n$message"
//
//        val keyboard = InlineKeyboardMarkup(listOf(
//            listOf(InlineButton("Создать новый отчет", InlineButton.NEW_REPORT)),
//            listOf(InlineButton("Все отчеты", InlineButton.REPORTS)),
//            listOf(InlineButton("Настройки", InlineButton.SETTINGS)),
//        ))
//
//        if (isEdit) {
//            user.editMessage(this.message.messageId, text, keyboard, parseMode = ParseMode.HTML)
//        } else {
//            user.sendMessage(text, keyboard, parseMode = ParseMode.HTML)
//        }
//    }

    fun sendRefreshStart(prefixMessage: String = "") {

        val userCount = Users.getPlayersCount()
        val readyUserCount = Users.getReadyPlayersCount()
        val message = "Всего зарегестрировалось ${endWordForNumWithNumber(userCount, listOf("игрок", "игрока", "игроков"))}\n" +
                "Готовы: $readyUserCount"

        val text = "$prefixMessage\n\n$message"

        val keyboard = if (userCount == readyUserCount && userCount > 1) {
            InlineKeyboardMarkup(listOf(
                listOf(InlineButton("Начать игру", InlineButton.START_GAME))
            ))
        } else null

        val admin = Users.getAdmin()

        if (admin != null) {
            if (admin.lastBotRefreshMessageId != null) {
                admin.editMessage(admin.lastBotRefreshMessageId!!, text, keyboard, parseMode = ParseMode.HTML)
            } else {
                admin.sendMessage(text, keyboard, parseMode = ParseMode.HTML)
            }
        }
    }

}

class EmptyEventHandler : EventHandler {
    override val user: User get() = throw NotImplementedError("")
    override val message: Message get() = throw NotImplementedError("")
    override fun handle() {
        //do nothing..
    }
}

@ExperimentalTime
object EventHandlerFactory {
    fun getHandler(update: Update): EventHandler {

        val user = update.message?.chat?.id?.let { id ->
            Users.getUserById(id)
        } ?: update.callbackQuery?.message?.chat?.id?.let { id ->
            Users.getUserById(id)
        } ?: update.message?.from?.let { user ->
            Users.addUser(user)
        }

        return when {
            user == null -> EmptyEventHandler()
            update.message != null -> MessageHandler(update.message, user)
            update.callbackQuery != null -> CallbackQueryHandler(update.callbackQuery, user)
            else -> EmptyEventHandler()
        }
    }
}