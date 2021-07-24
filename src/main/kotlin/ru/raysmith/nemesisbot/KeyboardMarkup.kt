package ru.raysmith.nemesisbot

import kotlinx.serialization.*
import kotlinx.serialization.json.encodeToJsonElement
import ru.raysmith.nemesisbot.network.TelegramApi
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@Polymorphic
@Serializable
sealed class KeyboardMarkup

val dateFormat = SimpleDateFormat("dd.MM.yyyy")
val dateTimeFormat = SimpleDateFormat("dd.MM HH:mm")
val dateTimeFormater = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val shortDateFormat = SimpleDateFormat("dd.MM")

class KeyboardCreator(private val pageFirstQuery: String, private val pageQuery: String, private val pageLastQuery: String) {

    companion object {

        const val PAGE_FIRST = -1
        const val PAGE_LAST = -2

        fun getYearRows(prefixCallbackData: String): MutableList<List<InlineButton>> {
            val calendar = Calendar.getInstance()
            return mutableListOf(calendar.get(Calendar.YEAR).let { y ->
                mutableListOf<InlineButton>().apply {
                    repeat(3) {
                        val yearString = (y - 2 + it).toString()
                        add(InlineButton(yearString, "$prefixCallbackData$yearString"))
                    }
                }
            })
        }

        fun getMonthRows(prefixCallbackData: String): MutableList<List<InlineButton>> {
            return mutableListOf(
                listOf(
                    InlineButton("Январь", "${prefixCallbackData}1"),
                    InlineButton("Февраль", "${prefixCallbackData}2"),
                    InlineButton("Март", "${prefixCallbackData}3"),
                    InlineButton("Апрель", "${prefixCallbackData}4"),
                ),
                listOf(
                    InlineButton("Май", "${prefixCallbackData}5"),
                    InlineButton("Июнь", "${prefixCallbackData}6"),
                    InlineButton("Июль", "${prefixCallbackData}7"),
                    InlineButton("Август", "${prefixCallbackData}8"),
                ),
                listOf(
                    InlineButton("Сентябрь", "${prefixCallbackData}9"),
                    InlineButton("Октябрь", "${prefixCallbackData}10"),
                    InlineButton("Ноябрь", "${prefixCallbackData}11"),
                    InlineButton("Декабрь", "${prefixCallbackData}12"),
                )
            )
        }

        fun getDayRows(year: Int, month: Int, prefixCallbackData: String, emptyDaySymbol: String = InlineButton.EMPTY): MutableList<List<InlineButton>> {
            val yearMonth = YearMonth.of(year, month)
            val prefixDays = yearMonth.atDay(1).dayOfWeek.value - 1
            val postfixDays = 7 - yearMonth.atEndOfMonth().dayOfWeek.value
            val fixedDays = (1..yearMonth.lengthOfMonth()).let { range ->
                mutableListOf<Int>().apply {
                    if (prefixDays > 0) {
                        repeat((1..prefixDays).count()) {
                            add(-1)
                        }
                    }
                    addAll(range)
                    if (postfixDays > 0) {
                        repeat((1..postfixDays).count()) {
                            add(-1)
                        }
                    }
                }
            }
            return mutableListOf<List<InlineButton>>().apply {
                fixedDays.chunked(7).forEach { chunk ->
                    add(chunk.map {
                        if (it == -1) InlineButton(" ", "$prefixCallbackData$emptyDaySymbol")
                        else InlineButton(it.toString(), "$prefixCallbackData$it")
                    })
                }
            }
        }
    }

    private var rows = 5
    private var columns = 1
    private var addPagesRow = true
    private var additionalRows: List<List<InlineButton>> = listOf()
    private var inEnd: Boolean = true

    private val max_displayed_pages = 5
    private val pages_paddings = max_displayed_pages / 2


    fun setRows(rows: Int): KeyboardCreator {
        this.rows = rows
        return this
    }

    fun setColumns(columns: Int): KeyboardCreator {
        this.columns = columns
        return this
    }

    fun setAddPagesRow(addPagesRow: Boolean): KeyboardCreator {
        this.addPagesRow = addPagesRow
        return this
    }

    fun addAdditionalRows(rows: List<List<InlineButton>>, inEnd: Boolean = true): KeyboardCreator {
        additionalRows = rows
        this.inEnd = inEnd
        return this
    }

    fun <T> create(from: Iterable<T>, pageN: Int, rowCreate: (T) -> InlineButton): InlineKeyboardMarkup? {
        if (from.count() == 0 && additionalRows.isEmpty()) return null
        val totalPages = (from.count() / (rows * columns)) + if (from.count() % (rows * columns) != 0) 1 else 0
        val page = when(pageN) {
            PAGE_FIRST -> 1
            PAGE_LAST -> totalPages
            else -> if (totalPages < pageN) 1 else if (pageN < 1) 1 else pageN
        }
        val lastIndex = ((page - 1) * rows * columns + rows * columns) - 1
        val isLastPage = lastIndex > from.count() - 1
        val range = IntRange((page - 1) * rows * columns, if (isLastPage) from.count() - 1 else lastIndex )
        return InlineKeyboardMarkup(from.filterIndexed { index, _ -> index in range }.let { items ->
            mutableListOf<List<InlineButton>>().apply {
                fun addAdditionalRows() = additionalRows.forEach { add(it) }
                if (!inEnd) {
                    addAdditionalRows()
                }

                items.chunked(columns).map { row ->
                    add(row.map { rowCreate(it) })
                }.apply {
                    if (addPagesRow && !(page == 1 && isLastPage)) {
                        val row = listOf(mutableListOf<InlineButton>().apply {
                            if (page > pages_paddings + 1 && totalPages > max_displayed_pages) add(InlineButton("«", pageFirstQuery))

                            var firstPage = when {
                                totalPages <= max_displayed_pages -> 1
                                page < pages_paddings + 1 -> 1
                                page == totalPages -> (page - pages_paddings) - 1
                                else -> page - pages_paddings
                            }
                            val lastPage = if ((firstPage + max_displayed_pages) - 1 > totalPages) totalPages else (firstPage + max_displayed_pages) - 1
                            (((lastPage - firstPage) - max_displayed_pages) + 1).let {
                                if (totalPages > max_displayed_pages && it != 0) firstPage += it
                            }
                            if (totalPages > 1) {
                                IntRange(firstPage, lastPage).forEach {
                                    add(InlineButton(if (it == page) "[$it]" else it.toString(), if (it == page) pageQuery else "$pageQuery$it"))
                                }
                            }

                            if (lastPage < totalPages) add(InlineButton("»", pageLastQuery))
                        })
                        addAll(row)
                    }
                }

                if (inEnd) {
                    addAdditionalRows()
                }
            }
        }
        )
    }
}

@Serializable
data class ReplyKeyboardMarkup(
    @SerialName("keyboard") @Required val keyboard: List<List<ReplyButton>>,
    @SerialName("resize_keyboard") @Required val resizeKeyboard: Boolean = true,
    @SerialName("one_time_keyboard") @Contextual val oneTimeKeyboard: Boolean? = null,
    @SerialName("selective") @Contextual val selective: Boolean? = null
) : KeyboardMarkup() {

    override fun toString(): String {
        return TelegramApi.json.encodeToJsonElement(this).toString()
    }
}

@Serializable
data class InlineKeyboardMarkup(
    @SerialName("inline_keyboard") @Required val keyboard: List<List<InlineButton>>
) : KeyboardMarkup() {
    override fun toString(): String {
        return TelegramApi.json.encodeToJsonElement(this).toString()
    }
}

@Serializable
data class ReplyKeyboardRemove(
    @SerialName("remove_keyboard") @Required val removeKeyboard: Boolean = true,
    @SerialName("selective") @Contextual val selective: Boolean? = null
) : KeyboardMarkup(){
    override fun toString(): String {
        return TelegramApi.json.encodeToJsonElement(this).toString()
    }
}

@Serializable
data class InlineButton(
    @SerialName("text") @Required val text: String,
    @SerialName("callback_data") @Contextual val callbackData: String? = null,
    @SerialName("url") @Contextual val url: String? = null,
) {

    // max length: 1 – 64
    companion object {
        const val EMPTY = " "
        const val MENU = "menu"
        const val NOTHING = "nothing"
        const val TODO = ""

        const val REFRESH_START = "refresh_start"
        const val START_GAME = "start_game"
        const val GOAL_NAME = "goal_name_"
        const val END = "end"
    }
}

@Serializable
data class ReplyButton(
    val text: String,
    @SerialName("request_contact") val requestContact: Boolean? = null,
    @SerialName("request_location") val requestLocation: Boolean? = null
) {

    companion object {
        const val BACK = "Назад"
        const val ADD = "Добавить"
        const val CANCEL = "Отмена"
        const val APPLY = "Подтвердить"
        const val TO_MENU = "В меню"
        const val SKIP = "Пропустить"
        const val DELETE = "Удалить"
        const val YES = "Да"
        const val NO = "Нет"
        const val CONTINUE = "Продолжить"
    }

}