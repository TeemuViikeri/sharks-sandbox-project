package fi.tuni.tamk.tiko.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    /**
     * Base URL for the NHL API that will be expanded by endpoints.
     */
    private val baseURL: String = "https://statsapi.web.nhl.com"

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
    private val teamEndpoint: String = "/api/v1/teams/"

    /**
     * Expand modifier for a team endpoint that shows roster of
     * active players for the specified team.
     */
    private val rosterExpand: String = "team.roster"

    /**
     * Schedule endpoint for fetching media.
     */
    private val scheduleEndpoint: String = "/api/v1/schedule"

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
     *
     * Possibly used later in development.
     */
    val boxscoreEndpoint: String = "/api/v1game/$gameID/boxscore"

    /**
     * Endpoint that returns basic post-game stats of
     * each period and last on-ice information.
     *
     * Possibly used later in development.
     */
    val linescoreEndpoint: String = "/api/v1game/$gameID/linescore"

    /**
     * Content endpoint that includes game media including
     * previews, videos, pictures etc.
     *
     * Possibly used later in development.
     */
    val contentEndpoint: String = "/api/v1game/$gameID/content"

    /**
     * Expand modifier that returns only editorial preview content.
     * Includes projected lineups via generated token objects.
     *
     * Possibly used later in development.
     */
    val previewContentExpand: String = "schedule.game.content.editorial.preview"

    /**
     * Expand modifier that returns only media content.
     * Includes extended highlights.
     */
    private val mediaExpand: String = "schedule.game.content.media.epg"

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
     * VideoView for showing extended highlights of a previous match.
     */
    private lateinit var extendedHighlights: VideoView

    /**
     * Variable which holds a video playback position over lifecycle states.
     */
    private var videoPlaybackPosition = 0

    companion object {
        /**
         * Key for playback time in the instance state bundle.
         */
        const val PLAYBACK_TIME: String = "play_time"
    }

    /**
     * Lifecycle method. Makes the initial API calls to the NHL API to display
     * team roster info when application has been loaded after start.
     *
     * @param savedInstanceState Bundle state data that is used to create
     * the initial activity. Includes video playback position.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("videoplayback", "onCreate")

        Log.d("videoplayback", savedInstanceState?.getInt(PLAYBACK_TIME).toString())

        if (savedInstanceState != null) {
            videoPlaybackPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        title = findViewById(R.id.tvTitle)
        rvRoster = findViewById(R.id.rvRoster)
        extendedHighlights = findViewById(R.id.previousMatchExtendedHighlights)

        val apiURL = "$baseURL$teamEndpoint$teamID?expand=" +
                "$rosterExpand,$scheduleNextExpand,$schedulePreviousExpand"

        val http = HttpConnection()

        http.fetchAsync(apiURL, this) {
            // Get Team object from JSON
            val team = convertJsonToTeamObject(it)
            // Set title of the activity to be the name of the team
            title.text = team.name

            // Setup roster, Adapter and RecyclerView
            val roster = team.roster.roster
            rosterAdapter = RosterAdapter(roster, this)
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

            setupNextMatchInfo(team)
            setupPreviousMatchInfo(team)
        }
    }

    override fun onResume() {
        super.onResume()
        setVideoPlayerToPosition(extendedHighlights)
    }

    /**
     * Pauses the video when build version is less than
     * Android N / Android 7.0 / API 24 and onPause() is called. Also saves
     * current video playback position.
     *
     * In newer versions application may be paused via super.onPause() but
     * the video may not be if multi-window or picture-in-picture mode is on.
     * Pausing video at this moment also prevents sound from video playing
     * after app has closed visually and before onStop() has been called.
     */
    override fun onPause() {
        super.onPause()

        videoPlaybackPosition = extendedHighlights.currentPosition

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            extendedHighlights.pause();
        }
    }

    /**
     * Lifecycle method. Stops video player and releases resources used
     * in the VideoView when onStop() is called.
     */
    override fun onStop() {
        super.onStop()
        releaseVideoPlayer(extendedHighlights)
    }

    /**
     *
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYBACK_TIME, videoPlaybackPosition)
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
     * Converts JSON data of a game schedule from NHL API to Jackson mapped Team object.
     *
     * @param json Returned JSON data from the NHL API's schedule endpoint.
     * @return Converted GameScheduleData object.
     */
    private fun convertJsonToScheduleObject(json: String): GameScheduleData {
        val mp = ObjectMapper()
        return mp.readValue(json, GameScheduleData::class.java)
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

    /**
     * Uses Calendar to get previous day from the extended date.
     *
     * @return Previous day from the extended date.
     */
    private fun java.util.Date.getPreviousDay(): java.util.Date {
        val cal = Calendar.getInstance()
        cal.time = this
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return cal.time
    }

    /**
     * Sets URI path to video player and sets video to certain frame.
     *
     * @param player VideoView to be initialized.
     * @param path URI path to video over the internet.
     * @param frame Preview image frame.
     */
    private fun initializeVideoPlayer(player: VideoView, path: String, frame: Int) {
        player.setVideoPath(path)
        setVideoPlayerToPosition(player, frame)
    }

    /**
     * Sets video to certain frame.
     *
     * @param player VideoView to be initialized.
     * @param frame To what frame video is set.
     */
    private fun setVideoPlayerToPosition(player: VideoView, frame: Int = 1000) {
        if (videoPlaybackPosition > 0) {
            player.seekTo(videoPlaybackPosition)
        } else {
            player.seekTo(frame)
        }
    }

    /**
     * Stops video player and releases resources VideoView is using.
     *
     * @param player VideoView that will be stopped.
     */
    private fun releaseVideoPlayer(player: VideoView) {
        player.stopPlayback()
    }

    /**
     * Adds MediaController to VideoView.
     *
     * @param videoView VideoView that the MediaController is added on.
     */
    private fun addMediaController(videoView: VideoView) {
        val mediaController = MediaController(this)
        mediaController.setMediaPlayer(videoView)
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(extendedHighlights)
    }

    /**
     * Sets up team's next match information to UI.
     *
     * @param team The team which info should be shown.
     */
    private fun setupNextMatchInfo(team: Team) {
        val date = team.nextGameSchedule.dates[0].date
        val formattedDate = date.toDate("yyyy-MM-dd").let {
                it1 -> it1?.formatTo("dd MMM yyyy")
        }
        val matchDate: TextView = findViewById(R.id.tvNextMatchDate)
        matchDate.text = formattedDate.toString()

        val awayTeam: TextView = findViewById(R.id.tvNextAway)
        val homeTeam: TextView = findViewById(R.id.tvNextHome)
        val awayTeamRecord: TextView = findViewById(R.id.tvNextAwayRecord)
        val homeTeamRecord: TextView = findViewById(R.id.tvNextHomeRecord)
        val nextMatchVenue: TextView = findViewById(R.id.nextMatchVenue)

        val nextGame = team.nextGameSchedule.dates[0].games[0]

        val isCurrentTeamAway: Boolean =
            nextGame.teams.away.team.id == team.id

        val http = HttpConnection()

        if (isCurrentTeamAway) {
            awayTeam.text = team.abbreviation

            val otherTeamId = nextGame.teams.home.team.id
            val otherTeamUrl = "$baseURL$teamEndpoint$otherTeamId"

            http.fetchAsync(otherTeamUrl, this) {
                val otherTeam = convertJsonToTeamObject(it)
                homeTeam.text = otherTeam.abbreviation
            }
        } else {
            val otherTeamId = nextGame.teams.away.team.id
            val otherTeamUrl = "$baseURL$teamEndpoint$otherTeamId"

            homeTeam.text = team.abbreviation
            http.fetchAsync(otherTeamUrl, this) {
                val otherTeam = convertJsonToTeamObject(it)
                awayTeam.text = otherTeam.abbreviation
            }
        }

        val awayTeamSeasonRecords = nextGame.teams.away.leagueRecord
        val awayWins = awayTeamSeasonRecords.wins.toString()
        val awayLosses = awayTeamSeasonRecords.losses.toString()
        val awayOts = awayTeamSeasonRecords.ot.toString()
        awayTeamRecord.text = "$awayWins-$awayLosses-$awayOts"

        val homeTeamSeasonRecords =
            nextGame.teams.home.leagueRecord
        val homeWins = homeTeamSeasonRecords.wins.toString()
        val homeLosses = homeTeamSeasonRecords.losses.toString()
        val homeOts = homeTeamSeasonRecords.ot.toString()
        homeTeamRecord.text = "$homeWins-$homeLosses-$homeOts"

        val matchVenue = nextGame.venue.name
        nextMatchVenue.text = "@ $matchVenue"
    }

    /**
     * Sets up team's next match information to UI.
     *
     * @param team The team which info should be shown.
     */
    private fun setupPreviousMatchInfo(team: Team) {
        val date = team.previousGameSchedule.dates[0].date
        val formattedDate = date.toDate("yyyy-MM-dd").let {
                it1 -> it1?.formatTo("dd MMM yyyy")
        }
        val matchDate: TextView = findViewById(R.id.tvPreviousMatchDate)
        matchDate.text = formattedDate.toString()

        val awayTeam: TextView = findViewById(R.id.tvPreviousAway)
        val homeTeam: TextView = findViewById(R.id.tvPreviousHome)
        val awayTeamResult: TextView = findViewById(R.id.tvPreviousAwayResult)
        val homeTeamResult: TextView = findViewById(R.id.tvPreviousHomeResult)
        val venue: TextView = findViewById(R.id.previousMatchVenue)

        val previousGame = team.previousGameSchedule.dates[0].games[0]

        val isCurrentTeamAway: Boolean =
            previousGame.teams.away.team.id == team.id

        val http = HttpConnection()

        if (isCurrentTeamAway) {
            awayTeam.text = team.abbreviation

            val otherTeamId = previousGame.teams.home.team.id
            val otherTeamUrl = "$baseURL$teamEndpoint$otherTeamId"

            http.fetchAsync(otherTeamUrl, this) {
                val otherTeam = convertJsonToTeamObject(it)
                homeTeam.text = otherTeam.abbreviation
            }
        } else {
            val otherTeamId = previousGame.teams.away.team.id
            val otherTeamUrl = "$baseURL$teamEndpoint$otherTeamId"

            homeTeam.text = team.abbreviation
            http.fetchAsync(otherTeamUrl, this) {
                val otherTeam = convertJsonToTeamObject(it)
                awayTeam.text = otherTeam.abbreviation
            }
        }

        awayTeamResult.text = previousGame.teams.away.score.toString()
        homeTeamResult.text = previousGame.teams.home.score.toString()

        val nextGameDate = team.nextGameSchedule.dates[0].date
        val nextGameDateFormatted =
            nextGameDate.toDate("yyyy-MM-dd")?.getPreviousDay().let {
                it1 -> it1?.formatTo("yyyy-MM-dd")
        }

        val scheduleURL = "$baseURL$scheduleEndpoint" +
                "?teamId=$teamID" +
                "&startDate=$date&endDate=${nextGameDateFormatted}" +
                "&expand=$mediaExpand"

        http.fetchAsync(scheduleURL, this) {
            val schedule = convertJsonToScheduleObject(it)
            val game = schedule.dates[0].games[0]
            val media = game.content.media
            val path = media.epg[2].items[0].playbacks[2].url
            Log.d("schedule", path)
            initializeVideoPlayer(extendedHighlights, path, 1000)
            addMediaController(extendedHighlights)
        }

        venue.text = "@ ${previousGame.venue.name}"
    }

}