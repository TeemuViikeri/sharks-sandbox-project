package fi.tuni.tamk.tiko.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {

    /**
     * Base URL for the NHL API that will be expanded by endpoints.
     */
    private val baseURL: String = "https://statsapi.web.nhl.com/api/v1"

    /**
     * San Jose Sharks team ID in the API.
     */
    private val teamID: Int = 28

    /**
     * Example game ID from the game between SJS and COL (4.5.2021)
     */
    private val gameID: Int = 2020020823

    /**
     * Team endpoint for the Sharks in the API.
     */
    private val teamEndpoint: String = "teams/$teamID"

    /**
     * Expand modifier for a team endpoint that shows roster of
     * active players for the specified team.
     */
    private val rosterExpand: String = "team.roster"

    /**
     * Expand modifier that returns details of the upcoming game for a team.
     */
    private val scheduleNextExpand: String = "team.schedule.next"

    /**
     * Expand modifier that returns details of the previous game for a team.
     */
    private val schedulePreviousExpand: String = "team.schedule.previous"

    /**
     * Endpoint that returns post-game stats of both teams and their players.
     */
    val boxscoreEndpoint: String = "game/$gameID/boxscore"

    /**
     * Endpoint that returns basic post-game stats of
     * each period and last on-ice information.
     */
    val linescoreEndpoint: String = "game/$gameID/linescore"

    /**
     * Content endpoint that includes game media including
     * previews, videos, pictures etc.
     */
    val contentEndpoint: String = "game/$gameID/content"

    /**
     * Content endpoint expand that returns only editorial preview content.
     * Includes projected lineups via generated token objects.
     */
    val previewContentExpand: String = "schedule.game.content.editorial.preview"

    /**
     * Title text view for the activity. Displays an NHL team's name.
     */
    private lateinit var title: TextView

    /**
     * RecyclerView for displaying team's roster.
     */
    private lateinit var rvRoster: RecyclerView

    /**
     * Adapter for RecyclerView that displays the roster.
     */
    private lateinit var rosterAdapter: RosterAdapter

    /**
     * Lifecycle method. Makes the initial API calls to the NHL API to display
     * team roster info when application has been loaded after start.
     *
     * @param savedInstanceState Bundle state data that is used to create
     * the initial activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = findViewById(R.id.tvTitle)
        rvRoster = findViewById(R.id.rvRoster)

        val apiURL = "$baseURL/$teamEndpoint?expand=" +
                "$rosterExpand,$scheduleNextExpand,$schedulePreviousExpand"

        val http = HttpConnection()
        http.fetchAsync(apiURL, this) {
            // Get Team object from JSON
            val team = convertJsonToTeamObject(it)
            // Set title of the activity to be the name of the team
            title.text = team.name

            // Setup roster, Adapter and RecyclerView
            val roster = team.roster.roster
            rosterAdapter = RosterAdapter(roster)
            rosterAdapter.sortPlayersByJerseyNumber()
            rvRoster.adapter = rosterAdapter
            rvRoster.layoutManager = LinearLayoutManager(this)

            // Setup roster buttons and their onClickListeners
            val btnSortByJerseyNumber: Button = findViewById(R.id.btnSortByJerseyNumber)
            val btnSortByName: Button = findViewById(R.id.btnSortByName)
            val btnSortByPosition: Button = findViewById(R.id.btnSortByPosition)

            btnSortByJerseyNumber.setOnClickListener {
                rosterAdapter.sortPlayersByJerseyNumber()
            }

            btnSortByName.setOnClickListener {
                rosterAdapter.sortPlayersByName()
            }

            btnSortByPosition.setOnClickListener {
                rosterAdapter.sortPlayersByPosition()
            }

            // Setup roster list item dividers
            val drawable = ContextCompat.getDrawable(this, R.drawable.roster_divider)
            val divider = DividerItemDecoration(
                rvRoster.context, (rvRoster.layoutManager as LinearLayoutManager).orientation)
            drawable?.let { dr -> divider.setDrawable(dr) }
            rvRoster.addItemDecoration(divider)

            // Setup next match information
            val awayTeam: TextView = findViewById(R.id.tvAway)
            val homeTeam: TextView = findViewById(R.id.tvHome)
            val awayTeamRecord: TextView = findViewById(R.id.tvAwayRecord)
            val homeTeamRecord: TextView = findViewById(R.id.tvHomeRecord)
            val nextMatchVenue: TextView = findViewById(R.id.nextMatchVenue)

            awayTeam.text = team.nextGameSchedule.dates[0].games[0].teams.away.team.name
            homeTeam.text = team.nextGameSchedule.dates[0].games[0].teams.home.team.name

            val awayTeamSeasonRecords = team.nextGameSchedule.dates[0].games[0].teams.away.leagueRecord
            val awayWins = awayTeamSeasonRecords.wins.toString()
            val awayLosses = awayTeamSeasonRecords.losses.toString()
            val awayOts = awayTeamSeasonRecords.ot.toString()
            awayTeamRecord.text = "$awayWins-$awayLosses-$awayOts"

            val homeTeamSeasonRecords = team.nextGameSchedule.dates[0].games[0].teams.home.leagueRecord
            val homeWins = homeTeamSeasonRecords.wins.toString()
            val homeLosses = homeTeamSeasonRecords.losses.toString()
            val homeOts = homeTeamSeasonRecords.ot.toString()
            homeTeamRecord.text = "$homeWins-$homeLosses-$homeOts"

            val matchDate = team.nextGameSchedule.dates[0].date
            val formattedDate = matchDate.toDate("yyyy-MM-dd").let {
                    it1 -> it1?.formatTo("dd MMM yyyy")
            }
            Log.d("matchdate", formattedDate.toString())
            val matchVenue = team.nextGameSchedule.dates[0].games[0].venue.name
            nextMatchVenue.text = "${formattedDate.toString()} @ $matchVenue"
        }
    }

    /**
     * Converts JSON data of a team from NHL API to Jackson mapped Team object.
     *
     * @param json Returned JSON data from the NHL API's team endpoint.
     * @return Converted Team object.
     */
    private fun convertJsonToTeamObject(json: String): Team {
        val mp = ObjectMapper()
        val teamData: TeamData = mp.readValue(json, TeamData::class.java)
        return teamData.teams[0]
    }

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
    private fun String.toDate(
        dateFormat: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): java.util.Date? {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    /**
     * Formats a Date object into a string representation with preferred
     * date format and time zone.
     *
     * @param dateFormat Preferred date format of the formatted date string.
     * @param timeZone Time zone used in the formatting.
     * @return String representation of a Date object.
     */
    private fun java.util.Date.formatTo(
        dateFormat: String,
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
}