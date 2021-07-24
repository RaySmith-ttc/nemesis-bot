package ru.raysmith.nemesisbot

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.raysmith.nemesisbot.network.TelegramApi
import ru.raysmith.nemesisbot.network.TelegramApiException
import ru.raysmith.nemesisbot.tg.User
import ru.raysmith.nemesisbot.tg.UserRole
import kotlin.properties.Delegates
import kotlin.time.ExperimentalTime

lateinit var ADMIN_TAG: String

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    println(args.toList())
    require(args.size >= 2 && args.contains("-admin")) { "" }

    ADMIN_TAG = args[args.indexOf("-admin") + 1]

    val logger = LoggerFactory.getLogger("main")
    var lastUpdate = 0L

    while (true) {
        try {
            val updates = TelegramApi.service.getUpdates(offset = lastUpdate + 1).execute()
            if (updates.isSuccessful && updates.body()!!.result.isNotEmpty()) {
                updates.body()!!.result.forEach { update ->
                    GlobalScope.launch {
                        EventHandlerFactory.getHandler(update).handle()
                    }
                }

                lastUpdate = updates.body()!!.result.last().updateId
            }
        } catch (e: TelegramApiException) {
            logger.error("Can't get updates", e)
            lastUpdate =+ 1
        } catch (e: Exception) {
            logger.error("Error while get updates", e)
            lastUpdate =+ 1
        }
    }
}