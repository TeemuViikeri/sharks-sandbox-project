package fi.tuni.tamk.tiko.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.util.*


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
            setupPlayerChart()

            http.fetchAsync(playerURL, this) { playerJson ->
                val player = convertJsonToPlayerObject(playerJson)
                setupBasicPlayerInfo(player)
                setupBasicPlayerStats(playerURL)
            }
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
        val games: TextView = findViewById(R.id.tvPlayerBasicGamesNumber)
        val goals: TextView = findViewById(R.id.tvPlayerBasicGoalsNumber)
        val assists: TextView = findViewById(R.id.tvPlayerBasicAssistsNumber)
        val points: TextView = findViewById(R.id.tvPlayerBasicPointsNumber)

        val playerSeasonStatsURL = "$playerURL/stats$statsSingleSeasonURL$currentSeason"

        val http = HttpConnection()

        http.fetchAsync(playerSeasonStatsURL, this) {
            val stats = convertJsonToStatsObject(it)
            val playerStats = stats.splits[0].stat
            games.text = playerStats.games.toString()
            goals.text = playerStats.goals.toString()
            assists.text = playerStats.assists.toString()
            points.text = playerStats.points.toString()
        }
    }

    /**
     *
     */
    private fun getBarEntries() : ArrayList<BarEntry> {
        val barEntries = ArrayList<BarEntry>()

        barEntries.add(BarEntry(1f, 4f))
        barEntries.add(BarEntry(2f, 6f))
        barEntries.add(BarEntry(3f, 8f))
        barEntries.add(BarEntry(4f, 2f))
        barEntries.add(BarEntry(5f, 4f))
        barEntries.add(BarEntry(6f, 1f))

        Log.d("chart", barEntries.toString())

        return barEntries
    }

    /**
     *
     */
    private fun setupPlayerChart() {
        val entries = getBarEntries()

        val barDataSet = BarDataSet(entries, "Points")
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 16f
        barDataSet.setColors(ContextCompat.getColor(this, R.color.teal))

        val data = BarData(barDataSet)

        val barChart: BarChart = findViewById(R.id.playerStatsChart)
        barChart.data = data
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBorders(false)
        barChart.invalidate()
    }
}
