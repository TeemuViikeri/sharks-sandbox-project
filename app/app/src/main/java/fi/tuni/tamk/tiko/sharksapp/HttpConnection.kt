package fi.tuni.tamk.tiko.sharksapp

import android.app.Activity
import java.net.URL
import kotlin.concurrent.thread

class HttpConnection {

    /**
     * Fetches content from URL and calls a callback function on the content.
     *
     * @param url URL of the content.
     * @param activity Activity context.
     * @param callback Callback function.
     */
    fun fetchAsync(url: String, activity: Activity, callback: (String) -> Unit) {
        thread {
            val apiURL = URL(url)
            val json = apiURL.readText()

            activity.runOnUiThread {
                callback(json)
            }
        }
    }
}