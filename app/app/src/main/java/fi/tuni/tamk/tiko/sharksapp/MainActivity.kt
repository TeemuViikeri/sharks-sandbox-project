package fi.tuni.tamk.tiko.sharksapp

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    /**
     * San Jose Sharks team ID in the API.
     */
    private val teamID: Int = 28

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
     * VideoView's MediaController for video controls.
     */
    private lateinit var videoMediaController: MediaController

    /**
     * Root scroll view.
     */
    private lateinit var scrollView: ScrollView

    /**
     * Variable which holds a video playback position over lifecycle states.
     */
    private var videoPlaybackPosition = 0

    /**
     * Holds video path.
     */
    private var videoPath: String = ""

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            videoPlaybackPosition = savedInstanceState.getInt(PLAYBACK_TIME)
        }

        Log.d("onCreate", videoPlaybackPosition.toString())

        title = findViewById(R.id.tvTitle)
        rvRoster = findViewById(R.id.rvRoster)
        extendedHighlights = findViewById(R.id.previousMatchExtendedHighlights)

        scrollView = findViewById(R.id.scrollView)
        scrollView.post { scrollView.fullScroll(View.FOCUS_UP) }

        val apiURL = UrlCompanion.BASE_URL +
                UrlCompanion.TEAM_ENDPOINT + "$teamID?expand=" +
                UrlCompanion.ROSTER_EXPAND + "," +
                UrlCompanion.SCHEDULE_NEXT_EXPAND + "," +
                UrlCompanion.SCHEDULE_PREVIOUS_EXPAND

        val http = HttpConnection()
        val converter = JsonConverter()

        http.fetchAsync(apiURL, this) {
            val team = converter.convertJsonToTeamObject(it)

            title.text = team.name

            val roster = team.roster.roster
            rosterAdapter = RosterAdapter(roster, this)
            rosterAdapter.sortPlayersByJerseyNumber()
            rvRoster.adapter = rosterAdapter
            rvRoster.layoutManager = LinearLayoutManager(this)

            val btnSortByJerseyNumber: Button =
                findViewById(R.id.btnSortByJerseyNumber)
            val btnSortByName: Button =
                findViewById(R.id.btnSortByName)
            val btnSortByPosition: Button =
                findViewById(R.id.btnSortByPosition)

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
            val drawable = ContextCompat.getDrawable(
                this,
                R.drawable.roster_divider
            )
            val divider = DividerItemDecoration(
                rvRoster.context,
                (rvRoster.layoutManager as LinearLayoutManager).orientation)
            drawable?.let { dr -> divider.setDrawable(dr) }
            rvRoster.addItemDecoration(divider)

            setupNextMatchInfo(team)
            setupPreviousMatchInfo(team)
        }
    }

    /**
     * Lifecycle method. Sets video playback to the playback position
     * the video was left when onPause() was called.
     */
    override fun onResume() {
        super.onResume()
        setVideoPlayerToPosition(extendedHighlights)
    }

    /**
     * Lifecycle method. Pauses the video when onPause() is called.
     * Also saves current video playback position.
     */
    override fun onPause() {
        super.onPause()
        videoPlaybackPosition = extendedHighlights.currentPosition
        extendedHighlights.stopPlayback()
    }

    /**
     * Saves playback position into a state Bundle.
     *
     * @param outState The bundle where saved data is being put.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYBACK_TIME, extendedHighlights.currentPosition)
    }

    /**
     * Restores playback position from state Bundle and sets it
     * as VideoView's position.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        videoPlaybackPosition = savedInstanceState.getInt(PLAYBACK_TIME)
    }

    /**
     * Sets URI path to video player and sets video to certain frame.
     *
     * @param path URI path to video over the internet.
     */
    private fun initializeVideoPlayer(path: String) {
        extendedHighlights.setVideoPath(path)

        extendedHighlights.setOnPreparedListener {
            setVideoPlayerToPosition(extendedHighlights)
            addMediaController()

            extendedHighlights.setOnCompletionListener { mp ->
                setVideoPlayerToPosition(extendedHighlights)

                Log.d("videoPosition", mp.currentPosition.toString())
            }

            scrollView.viewTreeObserver
                .addOnScrollChangedListener {
                    Log.d("scrolling", "scrolling")
                    videoMediaController.hide()
                }
        }
    }

    /**
     * Sets video to playback position the video was left or to start.
     *
     * @param player VideoView to be initialized.
     */
    private fun setVideoPlayerToPosition(player: VideoView)
    {
        if (videoPlaybackPosition > 0) {
            player.seekTo(videoPlaybackPosition)
        } else {
            player.seekTo(1000)
        }
    }

    /**
     * Adds MediaController to VideoView.
     */
    private fun addMediaController() {
        videoMediaController = MyMediaController(this, extendedHighlights)
        extendedHighlights.setMediaController(videoMediaController)
        videoMediaController.setAnchorView(findViewById(R.id.videoConstraint))
        videoMediaController.setMediaPlayer(extendedHighlights)
    }

    /**
     * Sets up team's next match information to UI.
     *
     * If there are no next matches available, a message will be shown
     * regarding this situation.
     *
     * @param team The team which info should be shown.
     */
    private fun setupNextMatchInfo(team: Team) {
        val tvNextMatchDate: TextView = findViewById(R.id.tvNextMatchDate)

        if (team.nextGameSchedule.dates.isNotEmpty()) {
            val date = team.nextGameSchedule.dates[0].date

            val formatter = DateStringFormatter()
            val formattedDate = formatter.toDate(date, "yyyy-MM-dd").let {
                    it1 ->
                if (it1 != null) {
                    formatter.formatTo(it1, "dd MMM yyyy")
                }
            }

            tvNextMatchDate.text = formattedDate.toString()

            val awayTeam: TextView = findViewById(R.id.tvNextAway)
            val homeTeam: TextView = findViewById(R.id.tvNextHome)
            val awayTeamRecord: TextView = findViewById(R.id.tvNextAwayRecord)
            val homeTeamRecord: TextView = findViewById(R.id.tvNextHomeRecord)
            val nextMatchVenue: TextView = findViewById(R.id.nextMatchVenue)

            val nextGame = team.nextGameSchedule.dates[0].games[0]

            val isCurrentTeamAway: Boolean =
                nextGame.teams.away.team.id == team.id

            val http = HttpConnection()
            val converter = JsonConverter()

            if (isCurrentTeamAway) {
                awayTeam.text = team.abbreviation

                val otherTeamId = nextGame.teams.home.team.id
                val otherTeamUrl = UrlCompanion.BASE_URL +
                        UrlCompanion.TEAM_ENDPOINT +
                        "$otherTeamId"

                http.fetchAsync(otherTeamUrl, this) {
                    val otherTeam = converter.convertJsonToTeamObject(it)
                    homeTeam.text = otherTeam.abbreviation
                }
            } else {
                val otherTeamId = nextGame.teams.away.team.id
                val otherTeamUrl = UrlCompanion.BASE_URL +
                        UrlCompanion.TEAM_ENDPOINT +
                        "$otherTeamId"

                homeTeam.text = team.abbreviation
                http.fetchAsync(otherTeamUrl, this) {
                    val otherTeam = converter.convertJsonToTeamObject(it)
                    awayTeam.text = otherTeam.abbreviation
                }
            }

            val awayTeamSeasonRecords = nextGame.teams.away.leagueRecord
            val awayWins = awayTeamSeasonRecords.wins
            val awayLosses = awayTeamSeasonRecords.losses
            val awayOts = awayTeamSeasonRecords.ot
            awayTeamRecord.text = resources.getString(
                R.string.record, awayWins, awayLosses, awayOts
            )

            val homeTeamSeasonRecords =
                nextGame.teams.home.leagueRecord
            val homeWins = homeTeamSeasonRecords.wins
            val homeLosses = homeTeamSeasonRecords.losses
            val homeOts = homeTeamSeasonRecords.ot
            homeTeamRecord.text = resources.getString(
                R.string.record, homeWins, homeLosses, homeOts
            )

            val matchVenue = nextGame.venue.name
            nextMatchVenue.text = resources.getString(
                R.string.match_venue,
                "@", matchVenue)
        } else {
            val nextMatchConstraint: ConstraintLayout =
                findViewById(R.id.nextMatchConstraint)
            val nextMatchInfoConstraint: ConstraintLayout =
                findViewById(R.id.nextMatchInfoConstraint)
            nextMatchConstraint.removeView(nextMatchInfoConstraint)
            nextMatchConstraint.removeView(tvNextMatchDate)

            val tvNextMatch: TextView = findViewById(R.id.tvNextMatch)

            val noMatchAvailable = TextView(this)
            noMatchAvailable.id = View.generateViewId()
            noMatchAvailable.text = resources.getString(
                R.string.no_next_matches_available
            )
            noMatchAvailable.textSize = 16f
            noMatchAvailable.typeface = ResourcesCompat.getFont(this, R.font.montserrat_medium)
            nextMatchConstraint.addView(noMatchAvailable)

            val set = ConstraintSet()
            set.clone(nextMatchConstraint)

            set.connect(
                noMatchAvailable.id,
                ConstraintSet.TOP,
                tvNextMatch.id,
                ConstraintSet.BOTTOM,
                24
            )

            set.connect(
                noMatchAvailable.id,
                ConstraintSet.LEFT,
                nextMatchConstraint.id,
                ConstraintSet.LEFT,
                0
            )

            set.connect(
                noMatchAvailable.id,
                ConstraintSet.RIGHT,
                nextMatchConstraint.id,
                ConstraintSet.RIGHT,
                0
            )

            set.applyTo(nextMatchConstraint)
        }
    }

    /**
     * Sets up team's previous match information to UI.
     *
     * If there are no previous matches available, a message will be shown
     * regarding this situation.
     *
     * @param team The team which info should be shown.
     */
    private fun setupPreviousMatchInfo(team: Team) {
        val tvPreviousMatchDate: TextView = findViewById(R.id.tvPreviousMatchDate)

        if (team.previousGameSchedule.dates.isNotEmpty()) {
            val date = team.previousGameSchedule.dates[0].date

            val formatter = DateStringFormatter()
            val formattedDate = formatter.toDate(date, "yyyy-MM-dd")!!.let {
                it1 -> formatter.formatTo(it1, "dd MMM yyyy")
            }
            tvPreviousMatchDate.text = formattedDate

            val awayTeam: TextView = findViewById(R.id.tvPreviousAway)
            val homeTeam: TextView = findViewById(R.id.tvPreviousHome)
            val awayTeamResult: TextView =
                findViewById(R.id.tvPreviousAwayResult)
            val homeTeamResult: TextView =
                findViewById(R.id.tvPreviousHomeResult)
            val previousMatchVenue: TextView =
                findViewById(R.id.previousMatchVenue)

            val previousGame = team.previousGameSchedule.dates[0].games[0]

            val isCurrentTeamAway: Boolean =
                previousGame.teams.away.team.id == team.id

            val http = HttpConnection()
            val converter = JsonConverter()

            if (isCurrentTeamAway) {
                awayTeam.text = team.abbreviation

                val otherTeamId = previousGame.teams.home.team.id
                val otherTeamUrl = UrlCompanion.BASE_URL +
                        UrlCompanion.TEAM_ENDPOINT +
                        "$otherTeamId"

                http.fetchAsync(otherTeamUrl, this) {
                    val otherTeam = converter.convertJsonToTeamObject(it)
                    homeTeam.text = otherTeam.abbreviation
                }
            } else {
                val otherTeamId = previousGame.teams.away.team.id
                val otherTeamUrl = UrlCompanion.BASE_URL +
                        UrlCompanion.TEAM_ENDPOINT +
                        "$otherTeamId"

                homeTeam.text = team.abbreviation
                http.fetchAsync(otherTeamUrl, this) {
                    val otherTeam = converter.convertJsonToTeamObject(it)
                    awayTeam.text = otherTeam.abbreviation
                }
            }

            awayTeamResult.text = previousGame.teams.away.score.toString()
            homeTeamResult.text = previousGame.teams.home.score.toString()

            val scheduleURL = UrlCompanion.BASE_URL +
                    UrlCompanion.SCHEDULE_ENDPOINT +
                    "?teamId=" +
                    "$teamID" +
                    "&startDate=$date&endDate=$date" +
                    "&expand=" +
                    UrlCompanion.MEDIA_EXPAND

            http.fetchAsync(scheduleURL, this) {
                val schedule = converter.convertJsonToScheduleObject(it)
                val game = schedule.dates[0].games[0]
                val media = game.content.media
                val path = media.epg[2].items[0].playbacks[2].url
                videoPath = path
                Log.d("schedule", path)
                initializeVideoPlayer(videoPath)
            }

            val matchVenue = previousGame.venue.name
            previousMatchVenue.text = resources.getString(
                R.string.match_venue,
                "@", matchVenue
            )
        } else {
            val previousMatchConstraint: ConstraintLayout =
                findViewById(R.id.previousMatchConstraint)
            val previousMatchInfoConstraint: ConstraintLayout =
                findViewById(R.id.previousMatchInfoConstraint)
            val previousMatchExtendedHighlights: VideoView =
                findViewById(R.id.previousMatchExtendedHighlights)

            previousMatchConstraint.removeView(tvPreviousMatchDate)
            previousMatchConstraint.removeView(previousMatchInfoConstraint)
            previousMatchConstraint.removeView(previousMatchExtendedHighlights)

            val tvPreviousMatch: TextView = findViewById(R.id.tvPreviousMatch)

            val noMatchAvailable = TextView(this)
            noMatchAvailable.id = View.generateViewId()
            noMatchAvailable.text = resources.getString(R.string.no_previous_matches_available)
            previousMatchConstraint.addView(noMatchAvailable)

            val set = ConstraintSet()
            set.clone(previousMatchConstraint)

            set.connect(
                noMatchAvailable.id,
                ConstraintSet.TOP,
                tvPreviousMatch.id,
                ConstraintSet.BOTTOM,
                24
            )

            set.connect(
                noMatchAvailable.id,
                ConstraintSet.LEFT,
                previousMatchConstraint.id,
                ConstraintSet.LEFT,
                0
            )

            set.connect(
                noMatchAvailable.id,
                ConstraintSet.RIGHT,
                previousMatchConstraint.id,
                ConstraintSet.RIGHT,
                0
            )

            set.applyTo(previousMatchConstraint)
        }
    }
}