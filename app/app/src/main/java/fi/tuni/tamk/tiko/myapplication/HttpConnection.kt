package fi.tuni.tamk.tiko.myapplication

import android.app.Activity
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HttpConnection {
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