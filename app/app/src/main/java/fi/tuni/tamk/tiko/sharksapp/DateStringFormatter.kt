package fi.tuni.tamk.tiko.sharksapp

import java.text.SimpleDateFormat
import java.util.*

class DateStringFormatter {

    /**
     * Converts a date string into a parsed Date object.
     *
     * You can use this function to represent match dates in user's own
     * date format and time zone.
     *
     * @param dateFormat Date format used in the parsed date.
     * @param timeZone The time zone in which the parsed date should be in.
     * @return Parsed Date object.
     */
    fun toDate(
        date: String,
        dateFormat: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): java.util.Date? {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(date)
    }

    /**
     * Formats a Date object into a string representation with preferred
     * date format and time zone.
     *
     * @param dateFormat Preferred date format of the formatted date string.
     * @param timeZone Time zone used in the formatting.
     * @return String representation of a Date object.
     */
    fun formatTo(
        date: java.util.Date,
        dateFormat: String,
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    /**
     * Uses Calendar to get previous day from the extended date.
     *
     * @return Previous day from the extended date.
     */
    private fun getPreviousDay(
        date: java.util.Date
    ): java.util.Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return cal.time
    }
}