package fi.tuni.tamk.tiko.sharksapp

class UrlCompanion {
    companion object {
        /**
         * Base URL for the NHL API that will be expanded by endpoints.
         */
        const val BASE_URL: String = "https://statsapi.web.nhl.com"

        /**
         * Team endpoint for the Sharks in the API.
         */
        const val TEAM_ENDPOINT: String = "/api/v1/teams/"

        /**
         * Schedule endpoint for fetching media.
         */
        const val SCHEDULE_ENDPOINT: String = "/api/v1/schedule"

        /**
         * Endpoint that returns post-game stats for both teams.
         *
         * NOTE: needs a game id after the endpoint
         *
         * Possibly used later in development.
         */
        const val GAME_ENDPOINT: String = "/api/v1/game/"

        /**
         * Endpoint that returns post-game stats of teams and players.
         *
         * NOTE: needs /api/v1/game/$gameID/ endpoint before this endpoint.
         *
         * Possibly used later in development.
         */
        const val BOXSCORE_ENDPOINT: String = "boxscore"

        /**
         * Endpoint that returns basic post-game stats of
         * each period and last on-ice information.
         *
         * NOTE: needs /api/v1/game/$gameID/ endpoint before this endpoint.
         *
         * Possibly used later in development.
         */
        const val LINESCORE_ENDPOINT: String = "linescore"

        /**
         * Content endpoint that includes game media including
         * previews, videos, pictures etc.
         *
         * NOTE: needs /api/v1/game/$gameID/ endpoint before this endpoint.
         *
         * Possibly used later in development.
         */
        const val CONTENT_ENDPOINT: String = "content"

        /**
         * Stats endpoint for multiple stats expansions.
         * Return stats data of players.
         *
         * NOTE: This endpoint doesn't return stats yet.
         * Requires stats expansion parameters.
         */
        const val STATS_ENDPOINT: String = "/stats"

        /**
         * Returns data about current season.
         */
        const val CURRENT_SEASON_ENDPOINT: String =
            "https://statsapi.web.nhl.com/api/v1/seasons/current"


        /**
         * Expand modifier for a team endpoint that shows roster of
         * active players for the specified team.
         */
        const val ROSTER_EXPAND: String = "team.roster"

        /**
         * Expand modifier that returns details of an upcoming game for a team.
         */
        const val SCHEDULE_NEXT_EXPAND: String = "team.schedule.next"

        /**
         * Expand modifier that returns details of a previous game for a team.
         */
        const val SCHEDULE_PREVIOUS_EXPAND: String = "team.schedule.previous"

        /**
         * Expand modifier that returns only editorial preview content.
         * Includes projected lineups via generated token objects.
         *
         * Possibly used later in development.
         */
        const val PREVIEW_CONTENT_EXPAND: String =
            "schedule.game.content.editorial.preview"

        /**
         * Expand modifier that returns only media content.
         * Includes extended highlights.
         */
        const val MEDIA_EXPAND: String = "schedule.game.content.media.epg"

        /**
         * One of the parameters for stats endpoint.
         * Return single season stats of a player.
         *
         * NOTE: Requires: season parameter.
         */
        const val STATS_SINGLE_SEASON: String =
            "?stats=statsSingleSeason&season="

        /**
         * One of the parameters for stats endpoint.
         * Return single season game logs of a player.
         *
         * NOTE: Requires: season parameter.
         */
        const val STATS_GAME_LOG: String = "?stats=gameLog&season="
    }
}