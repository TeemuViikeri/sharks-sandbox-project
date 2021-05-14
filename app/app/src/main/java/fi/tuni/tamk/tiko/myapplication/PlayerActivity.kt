package fi.tuni.tamk.tiko.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper

class PlayerActivity : AppCompatActivity() {

    /**
     * Base URL for the NHL API that will be expanded by endpoints.
     */
    private val baseURL: String = "https://statsapi.web.nhl.com"

    /**
     *
     */
    private val statsSingleSeasonURL: String = "?stats=statsSingleSeason&season="

    /**
     *
     */
    private lateinit var currentSeason: String

    /**
     *
     */
    private val currentSeasonEndpoint: String = "https://statsapi.web.nhl.com/api/v1/seasons/current"

    /**
     * Title text view for the activity. Displays a player's name.
     */
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        title = findViewById(R.id.tvPlayerTitle)

        val intent = intent
        val playerLink = intent.getStringExtra("url").toString()
        val playerURL = "$baseURL$playerLink"

        val http = HttpConnection()

        http.fetchAsync(currentSeasonEndpoint, this) {
            val season = convertJsonToSeasonObject(it)
            currentSeason = season.seasonId
        }

        http.fetchAsync(playerURL, this) {
            val player = convertJsonToPlayerObject(it)
            setupBasicPlayerInfo(player)
            setupBasicPlayerStats(playerURL)
        }
    }

    /**
     * Converts JSON data of a player from NHL API to Jackson mapped Player object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Player object.
     */
    private fun convertJsonToPlayerObject(json: String): People {
        val mp = ObjectMapper()
        val playerData: PeopleData = mp.readValue(json, PeopleData::class.java)
        return playerData.people[0]
    }

    /**
     * Converts JSON data of a player from NHL API to Jackson mapped Player object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Player object.
     */
    private fun convertJsonToSeasonObject(json: String): Season {
        val mp = ObjectMapper()
        val seasonsData: Seasons = mp.readValue(json, Seasons::class.java)
        return seasonsData.seasons[0]
    }

    /**
     * Converts JSON data of a player from NHL API to Jackson mapped Player object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Player object.
     */
    private fun convertJsonToStatsObject(json: String): StatsInfo {
        val mp = ObjectMapper()
        val statsData: Stats = mp.readValue(json, Stats::class.java)
        Log.d("player_stats", statsData.toString())
        return statsData.stats[0]
    }

    /**
     *
     */
    private fun setupBasicPlayerInfo(player: People) {
        title.text = player.fullName
        val number: TextView = findViewById(R.id.tvPlayerInfoNumber)
        number.text = player.primaryNumber
        val age: TextView = findViewById(R.id.tvPlayerInfoAge)
        age.text = player.currentAge.toString()
        val nationality: TextView = findViewById(R.id.tvPlayerInfoNationality)
        nationality.text = player.nationality
        val height: TextView = findViewById(R.id.tvPlayerInfoHeight)
        height.text = player.height
        val weight: TextView = findViewById(R.id.tvPlayerInfoWeight)
        weight.text = "${player.weight} lbs"
        val shoots: TextView = findViewById(R.id.tvPlayerInfoShoots)
        shoots.text = player.shootsCatches
        val primaryPosition: TextView = findViewById(R.id.tvPlayerInfoPosition)
        primaryPosition.text = player.primaryPosition.name
    }

    /**
     *
     */
    private fun setupBasicPlayerStats(playerURL: String) {
        val goals: TextView = findViewById(R.id.tvPlayerBasicGoalsNumber)
        val assists: TextView = findViewById(R.id.tvPlayerBasicAssistsNumber)
        val points: TextView = findViewById(R.id.tvPlayerBasicPointsNumber)

        val playerSeasonStatsURL = "$playerURL/stats$statsSingleSeasonURL$currentSeason"

        val http = HttpConnection()

        Log.d("player_stats", playerSeasonStatsURL)

        http.fetchAsync(playerSeasonStatsURL, this) {
            Log.d("player_stats", it)
            val stats = convertJsonToStatsObject(it)
            val playerStats = stats.splits[0].stat
            goals.text = playerStats.goals.toString()
            assists.text = playerStats.assists.toString()
            points.text = playerStats.points.toString()
        }
    }
}
