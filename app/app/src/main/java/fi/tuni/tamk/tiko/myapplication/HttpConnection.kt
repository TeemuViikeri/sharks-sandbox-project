package fi.tuni.tamk.tiko.myapplication

import android.app.Activity
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HttpConnection {

    /**
     * Base URL for the NHL API that will be expanded by endpoints.
     */
    val baseURL: String = "https://statsapi.web.nhl.com"

    /**
     * San Jose Sharks team ID in the API.
     */
    val teamID: Int = 28

    /**
     * Example game ID from the game between SJS and COL (4.5.2021)
     */
    private val gameID: Int = 2020020823

    /**
     * Team endpoint for the Sharks in the API.
     */
    val teamEndpoint: String = "/api/v1/teams/"

    /**
     * Player endpoint for NHL players in the API.
     */
    val playerEndpoint: String = "/api/v1/people/"

    /**
     * Expand modifier for a team endpoint that shows roster of
     * active players for the specified team.
     */
    val rosterExpand: String = "team.roster"

    /**
     * Expand modifier that returns details of the upcoming game for a team.
     */
    val scheduleNextExpand: String = "team.schedule.next"

    /**
     * Expand modifier that returns details of the previous game for a team.
     */
    val schedulePreviousExpand: String = "team.schedule.previous"

    /**
     * Endpoint that returns post-game stats of both teams and their players.
     */
    val boxscoreEndpoint: String = "/api/v1game/$gameID/boxscore"

    /**
     * Endpoint that returns basic post-game stats of
     * each period and last on-ice information.
     */
    val linescoreEndpoint: String = "/api/v1game/$gameID/linescore"

    /**
     * Content endpoint that includes game media including
     * previews, videos, pictures etc.
     */
    val contentEndpoint: String = "/api/v1game/$gameID/content"

    /**
     * Expand modifier that returns only editorial preview content.
     * Includes projected lineups via generated token objects.
     */
    val previewContentExpand: String = "schedule.game.content.editorial.preview"

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