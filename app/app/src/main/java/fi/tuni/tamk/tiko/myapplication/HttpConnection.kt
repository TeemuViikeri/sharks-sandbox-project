package fi.tuni.tamk.tiko.myapplication

import android.app.Activity
import java.net.URL
import kotlin.concurrent.thread

class HttpConnection {

    /**
     *
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

    /**
     *
     */
    fun fetch(url: String, activity: Activity, callback: (String) -> Unit) {
        val apiURL = URL(url)
        val json = apiURL.readText()

        activity.runOnUiThread {
            callback(json)
        }
    }
}