package ru.raysmith.nemesisbot

fun getThreadId() = Thread.currentThread().name.let { th -> th.substring(th.lastIndexOf("-") + 1) }

fun List<Goal>.join(): String {
    val sb = StringBuilder()
    forEach { goal ->
        sb.append("<b>${goal.title}</b>: ${goal.description}\n\n")
    }
    return sb.toString()
}

fun List<Goal>.joinRow(): List<List<InlineButton>> {
    val res = mutableListOf<List<InlineButton>>()
    forEach { goal ->
        res.add(listOf(InlineButton(goal.title, InlineButton.GOAL_NAME + goal.name)))
    }
    return res
}