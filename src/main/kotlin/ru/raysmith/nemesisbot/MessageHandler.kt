package ru.raysmith.nemesisbot

import org.slf4j.LoggerFactory
import ru.raysmith.nemesisbot.tg.Message
import ru.raysmith.nemesisbot.tg.MessageType
import ru.raysmith.nemesisbot.tg.User
import ru.raysmith.nemesisbot.tg.UserRole
import ru.raysmith.utils.endWordForNumWithNumber
import kotlin.math.log
import kotlin.time.ExperimentalTime

class MessageHandler(override val message: Message, override val user: User) : EventHandler {

    val logger = LoggerFactory.getLogger("message-handler-${getThreadId()}")

    @ExperimentalTime
    override fun handle() {
        when (message.type) {
            MessageType.COMMAND -> handleCommand(userCommand = BotCommand.getByMessage(message))
            MessageType.TEXT -> handleTextMessage()
            else -> handleTextMessage()
        }
    }

    @ExperimentalTime
    private fun handleTextMessage() {
        logger.debug(user.toString())
        when (user.location) {
            Location.NAME -> {
                if (message.text?.isNotEmpty() == true) {
                    user.name = message.text
                    user.isReady = true
                    sendRefreshStart(prefixMessage = "${user.name} присоеденился.")
                    user.sendMessage("Ожидание начала игры...")
                }
            }
        }
    }

    private fun handleCommand(userCommand: BotCommand) {
        if (userCommand.adminOnly && user.role != UserRole.ADMIN) {
            // do nothing
        }

        when(userCommand) {
            BotCommand.START -> {
                if (user.role != UserRole.ADMIN) {
                    if (user.isReady) {
                        user.sendMessage("Вы уже в игре. Дождитесь, когда админ завершит ее")
                    } else {
                        if (!user.isInGame) {
                            user.sendMessage("Все места заняты")
                        } else {
                            user.location = Location.NAME
                            user.sendMessage("Введите имя")
                            sendRefreshStart()
                        }
                    }
                } else if (user.role == UserRole.ADMIN) {
                    Users.deleteAllUsers()
                    Users.getAdmin()?.location = Location.START
                    user.isReady = true
                    sendRefreshStart(prefixMessage = "Новая игра. Ожидание новых игроков...")
                }
            }
//            BotCommand.RESET -> {
//                if (user.role == UserRole.ADMIN) {
//                    Users.deleteAllUsers()
//                    user.location = Location.START
//                    user.isReady = true
//                    sendRefreshStart(prefixMessage = "Регистрации отменены. Ожидание новых игроков...")
//                }
//            }
            BotCommand.PLAYERS -> {
                val message = StringBuilder("${Users.getAdmin()}")
                Users.getPlayers().forEach {
                    message.append("\n\n$it")
                }
                user.sendMessage(message.toString())
            }
            else -> { }
        }
    }
}