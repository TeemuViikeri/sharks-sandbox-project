package fi.tuni.tamk.tiko.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper

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
     * Content endpoint expand that returns only editorial preview content,
     * especially projected lineups.
     */
    val previewContentExpand: String = "schedule.game.content.editorial.preview"

    /**
     *
     */
    private lateinit var title: TextView

    /**
     *
     */
    private lateinit var rvRoster: RecyclerView

    /**
     *
     */
    private lateinit var rosterAdapter: RosterAdapter

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = findViewById(R.id.tvTitle)
        rvRoster = findViewById(R.id.rvRoster)

        val apiURL = "$baseURL/$teamEndpoint?expand=" +
                "$rosterExpand,$scheduleNextExpand,$schedulePreviousExpand"

        Log.d("url", apiURL)

        val http = HttpConnection()
        http.fetchAsync(apiURL, this) {
            val team = convertJsonToTeamObject(it)
            Log.d("url", team.toString())
            title.text = team.name

            val roster = sortPlayersByJerseyNumber(team.roster.roster)
            
            rosterAdapter = RosterAdapter(roster)
            rvRoster.adapter = rosterAdapter
            rvRoster.layoutManager = LinearLayoutManager(this)

            val drawable = ContextCompat.getDrawable(this, R.drawable.roster_divider)
            val divider = DividerItemDecoration(
                rvRoster.context, (rvRoster.layoutManager as LinearLayoutManager).orientation)
            drawable?.let { dr -> divider.setDrawable(dr) }
            rvRoster.addItemDecoration(divider)

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

            val matchVenue = team.nextGameSchedule.dates[0].games[0].venue.name
            nextMatchVenue.text = "@$matchVenue"
        }
    }

    /**
     *
     */
    private fun convertJsonToTeamObject(json: String): Team {
        val mp = ObjectMapper()
        val teamData: TeamData = mp.readValue(json, TeamData::class.java)
        return teamData.teams[0]
    }
    
    private fun sortPlayersByJerseyNumber(players: List<Player>): List<Player> {
        return players.sortedBy { it.jerseyNumber.toInt() }
    }
}