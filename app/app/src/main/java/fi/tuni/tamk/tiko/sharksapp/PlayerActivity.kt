package fi.tuni.tamk.tiko.sharksapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import org.joda.time.DateTime


class PlayerActivity : AppCompatActivity() {

    /**
     * Title text view for the activity. Displays a player's name.
     */
    private lateinit var title: TextView

    /**
     * Variable for holding current season as String (i.e. 20202021).
     */
    private lateinit var currentSeason: String

    /**
     * Lifecycle method. Multiple API fetches is done. Activity includes three
     * parts after title (player name): basic player information, season stats
     * and points per months graph.
     *
     * @param savedInstanceState Bundle state data. Not used in this activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        title = findViewById(R.id.tvPlayerTitle)

        val intent = intent
        val playerLink = intent.getStringExtra("url").toString()
        val playerURL = "${UrlCompanion.BASE_URL}$playerLink"

        val http = HttpConnection()
        val converter = JsonConverter()

        http.fetchAsync(
            UrlCompanion.CURRENT_SEASON_ENDPOINT,
            this
        ) { seasonJson ->
            val season = converter.convertJsonToSeasonObject(seasonJson)
            currentSeason = season.seasonId

            http.fetchAsync(playerURL, this) { playerJson ->
                val player = converter.convertJsonToPlayerObject(playerJson)
                setupBasicPlayerInfo(player)
                setupBasicPlayerStats(playerURL)

                val gameLogURL = playerURL +
                        UrlCompanion.STATS_ENDPOINT +
                        UrlCompanion.STATS_GAME_LOG +
                        currentSeason

                http.fetchAsync(gameLogURL, this) { gameLogJson ->
                    val gameLogData =
                        converter.convertJsonToStatsObject(gameLogJson)
                    val gameLogs = gameLogData.splits.asReversed()
                    val entries = getEntries(gameLogs)
                    setupPlayerChart(entries)
                }
            }
        }
    }

    /**
     * Setups basic player info on the player profile.
     *
     * @param player People object that holds the data to be displayed.
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
        weight.text = resources.getString(R.string.lbs, player.weight)
        val shoots: TextView = findViewById(R.id.tvPlayerInfoShoots)
        shoots.text = player.shootsCatches
        val primaryPosition: TextView = findViewById(R.id.tvPlayerInfoPosition)
        primaryPosition.text = player.primaryPosition.name
    }

    /**
     * Setups basic player stats on the player profile:
     * games played, goals, assists, points.
     *
     * @param playerURL Player URL used to create fetch URL for this function.
     */
    private fun setupBasicPlayerStats(playerURL: String) {
        val games: TextView = findViewById(R.id.tvPlayerBasicGamesNumber)
        val goals: TextView = findViewById(R.id.tvPlayerBasicGoalsNumber)
        val assists: TextView = findViewById(R.id.tvPlayerBasicAssistsNumber)
        val points: TextView = findViewById(R.id.tvPlayerBasicPointsNumber)

        val playerSeasonStatsURL = playerURL +
                UrlCompanion.STATS_ENDPOINT +
                UrlCompanion.STATS_SINGLE_SEASON +
                currentSeason

        val http = HttpConnection()
        val converter = JsonConverter()

        http.fetchAsync(playerSeasonStatsURL, this) {
            val stats = converter.convertJsonToStatsObject(it)
            val playerStats = stats.splits[0].stat
            games.text = playerStats.games.toString()
            goals.text = playerStats.goals.toString()
            assists.text = playerStats.assists.toString()
            points.text = playerStats.points.toString()
        }
    }

    /**
     * Returns entries of player's points per months from season game logs.
     *
     * @param gameLogs Game logs that include game dates.
     * @return List of BarEntries for a chart.
     */
    private fun getEntries(gameLogs: List<Split>) : MutableList<BarEntry> {
        val entries: MutableList<BarEntry> = mutableListOf()

        val formatter = DateStringFormatter()

        var currentMonth = 0
        var pointsPerEntry = 0

        gameLogs.forEach {
            val gameDate = formatter.toDate(it.date, "yyyy-MM-dd")
            val dateTime = DateTime(gameDate)
            val month = dateTime.toString("MM").toInt() - 1

            val gamePoints = it.stat.points

            if (currentMonth != month) {
                val entry = BarEntry(
                    currentMonth.toFloat(), pointsPerEntry.toFloat()
                )

                entries.add(entry)

                currentMonth = month
                pointsPerEntry = 0
            }

            pointsPerEntry += gamePoints
        }

        val lastEntry = BarEntry(
            currentMonth.toFloat(), pointsPerEntry.toFloat()
        )

        entries.add(lastEntry)

        return entries
    }

    /**
     * Returns correct amount of month labels corresponding to entries.
     *
     * @param entries
     * @return List of month labels.
     */
    private fun getMonthLabels(entries: List<BarEntry>): List<String> {
        val start = entries[0].x.toInt()
        val end = entries.last().x.toInt() + 1

        val months = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )

        return months.subList(start, end)
    }

    /**
     * Setups a stat chart on the player profile.
     *
     * @param entries Data for the chart.
     */
    private fun setupPlayerChart(entries: MutableList<BarEntry>) {
        val barDataSet = BarDataSet(entries, "Points per month")
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 16f
        barDataSet.color = ContextCompat.getColor(this, R.color.teal)
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val data = BarData(barDataSet)

        val chart: BarChart = findViewById(R.id.playerStatsChart)
        chart.data = data
        chart.description.isEnabled = false
        chart.isHighlightFullBarEnabled = false
        chart.setTouchEnabled(false)

        val xAxis = chart.xAxis
        val xAxisLabels = getMonthLabels(entries)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.granularity = 1f
        chart.axisRight.setDrawGridLines(false)
        chart.axisRight.axisMinimum = 0f
        chart.axisRight.granularity = 1f

        chart.isHighlightFullBarEnabled = false
        chart.isHighlightPerTapEnabled = false
        chart.isHighlightPerDragEnabled = false
        chart.setDrawValueAboveBar(false)
        chart.setFitBars(true)
        chart.invalidate()
    }
}
