package ru.raysmith.nemesisbot

import org.slf4j.LoggerFactory
import ru.raysmith.nemesisbot.Users.users
import ru.raysmith.nemesisbot.tg.User
import ru.raysmith.nemesisbot.tg.UserRole

object Users {

    const val MAX_PLAYERS = 5

    private val logger = LoggerFactory.getLogger("users")
    private val users = mutableListOf<User>()

    fun getPlayersCount() = synchronized(this) { users.size }
    fun getReadyPlayersCount() = synchronized(this) { users.count { it.role == UserRole.PLAYER && it.isReady } + users.count { it.role == UserRole.ADMIN } }

    fun addUser(user: User): User {
        synchronized(this) {
            logger.debug("Add user #${user.id}")
            if (user.username == ADMIN_TAG) {
                user.role = UserRole.ADMIN
                user.name = "Админ"
            }

            if (user.role != UserRole.ADMIN) {
                user.isInGame = getPlayersCount() < MAX_PLAYERS
            }
            users.add(user)
        }
        return user
    }

    fun getUserById(id: Int): User? {
        synchronized(this) {
            return users.firstOrNull { it.id == id }
        }
    }

    fun deleteAllUsers() {
        synchronized(this) {
            val userCopy = users.toList()
            userCopy.onEach {
                if (it.role != UserRole.ADMIN) {
                    it.sendMessage("Игра закончена. Используйте команду /start чтобы начать новую")
                    users.remove(it)
                }
            }
        }
    }

    fun getAllUsers(): List<User> = users
    fun getAdmin(): User? = synchronized(this) {  users.firstOrNull { it.role == UserRole.ADMIN } }
    fun getPlayers(): List<User> = synchronized(this) {  users.filter { it.role == UserRole.PLAYER } }
    fun allPlayersSelectedGoal() = synchronized(this) {
        users.all { it.selectedGoal != null }
    }

}