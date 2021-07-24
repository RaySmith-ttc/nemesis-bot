package ru.raysmith.nemesisbot

import org.slf4j.LoggerFactory
import ru.raysmith.nemesisbot.tg.Message
import ru.raysmith.nemesisbot.network.TelegramApiException
import ru.raysmith.nemesisbot.tg.CallbackQuery
import ru.raysmith.nemesisbot.tg.User
import ru.raysmith.nemesisbot.tg.UserRole
import kotlin.time.ExperimentalTime

@ExperimentalTime
class CallbackQueryHandler(val query: CallbackQuery, override val user: User) : EventHandler {

    val logger = LoggerFactory.getLogger("callback-query-handler-${getThreadId()}")

    override val message: Message = query.message

    fun randomGoals(): List<Goal> {
        val playersCount = Users.getPlayersCount()
        val res = mutableListOf<Goal>()
        val blackList = synchronized(Users) { Users.getAllUsers().map { it.goals }.flatten() }
        res.add(Goal.values().filter { it.type == GoalType.LOCAL && it.playersRequired <= playersCount && it !in blackList }.random())
        res.add(Goal.values().filter { it.type == GoalType.CORPORATE && it.playersRequired <= playersCount && it !in blackList }.random())

        logger.debug("Goals ($playersCount): $res")

        return res
    }

    override fun handle() {
        logger.debug("Receive query: ${query.data}")
        try {
            when {
                query.data == InlineButton.START_GAME -> {
                    Users.getPlayers().forEach { u ->
                        val goals = randomGoals()
                        u.goals = goals
                        u.editMessage(u.lastBotMessageId!!, goals.join())
                    }

                    val goals = randomGoals()
                    user.goals = goals
                    Users.getAdmin()!!.sendMessage(goals.join(), InlineKeyboardMarkup(goals.joinRow()))
                }
                query.data.startsWith(InlineButton.GOAL_NAME) -> {
                    val goal = Goal.valueOf(query.data.substring(InlineButton.GOAL_NAME.length))
                    user.selectedGoal = goal

                    if (user.role == UserRole.ADMIN) {
                        Users.getPlayers().forEach { u ->
                            u.editMessage(u.lastBotMessageId!!, u.goals.join(), InlineKeyboardMarkup(u.goals.joinRow()))
                        }
                        user.editMessage(message, "<b>${user.selectedGoal!!.title}</b>: ${user.selectedGoal!!.description}\n\n")
                        user.sendMessage("Цель выбрана")
                    } else {
                        user.editMessage(message, "<b>${user.selectedGoal!!.title}</b>: ${user.selectedGoal!!.description}\n\n")
                        user.sendMessage("Цель выбрана. Ожидание конца игры...")
                        if (Users.allPlayersSelectedGoal()) {
                            Users.getAdmin()?.let { admin ->
                                admin.editMessage(admin.lastBotMessageId!!, "Цель выбрана", InlineKeyboardMarkup(listOf(
                                    listOf(InlineButton("Конец игры", InlineButton.END))
                                )))
                            }
                        }
                    }
                }
                query.data == InlineButton.END -> {
                    val message = StringBuilder("Админ выбрал цель: <b>${Users.getAdmin()?.selectedGoal?.title}</b> [<i>${Users.getAdmin()?.selectedGoal?.type?.value}</i>]\n${Users.getAdmin()?.selectedGoal?.description}\n\n")
                    Users.getPlayers().forEach {
                        message.append(it.name)
                        message.append(" выбрал цель: <b>")
                        message.append(it.selectedGoal!!.title)
                        message.append("</b> [<i>")
                        message.append(it.selectedGoal!!.type.value)
                        message.append("</i>]\n")
                        message.append(it.selectedGoal!!.description)
                        message.append("\n\n")
                    }

                    Users.getAdmin()?.editMessage(query.message, "$message\n\nИспользуйте /start чтобы начать новую игру.")
                    Users.getPlayers().forEach {
                        it.sendMessage(message.toString())
                    }
                }

                else -> query.answer()
            }
            query.answer()
        } catch (e: TelegramApiException) {
                if (e.message == "Bad Request: message is not modified: specified new message content and reply markup are exactly the same as a current content and reply markup of the message") {
                    query.answer()
                } else throw e
            }
    }
}

