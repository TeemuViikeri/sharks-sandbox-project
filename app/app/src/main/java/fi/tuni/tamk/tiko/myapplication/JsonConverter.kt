package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.databind.ObjectMapper

class JsonConverter {

    /**
     * Converts JSON team data from NHL API to Jackson mapped Team object.
     *
     * @param json Returned JSON data from the NHL API's team endpoint.
     * @return Converted Team object.
     */
    fun convertJsonToTeamObject(json: String): Team {
        val mp = ObjectMapper()
        val teamData: TeamData = mp.readValue(json, TeamData::class.java)
        return teamData.teams[0]
    }

    /**
     * Converts JSON game schedule data from NHL API to
     * Jackson mapped GameScheduleData object.
     *
     * @param json Returned JSON data from the NHL API's schedule endpoint.
     * @return Converted GameScheduleData object.
     */
    fun convertJsonToScheduleObject(json: String): GameScheduleData {
        val mp = ObjectMapper()
        return mp.readValue(json, GameScheduleData::class.java)
    }

    /**
     * Converts JSON player data from NHL API to
     * Jackson mapped Player object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Player object.
     */
    fun convertJsonToPlayerObject(json: String): People {
        val mp = ObjectMapper()
        val playerData: PeopleData = mp.readValue(json, PeopleData::class.java)
        return playerData.people[0]
    }

    /**
     * Converts JSON season data from NHL API to
     * Jackson mapped Season object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Season object.
     */
    fun convertJsonToSeasonObject(json: String): Season {
        val mp = ObjectMapper()
        val seasonsData: Seasons = mp.readValue(json, Seasons::class.java)
        return seasonsData.seasons[0]
    }

    /**
     * Converts JSON player stats data from NHL API to
     * Jackson mapped StatsInfo object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Player object.
     */
    fun convertJsonToStatsObject(json: String): StatsInfo {
        val mp = ObjectMapper()
        val statsData: Stats = mp.readValue(json, Stats::class.java)
        return statsData.stats[0]
    }
}