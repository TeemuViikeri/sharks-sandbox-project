package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Team(
    val name: String = "",
    val abbreviation: String = "",
    val teamName: String = "",
    val locationName: String = "",
    val division: Division = Division(),
    val conference: Conference = Conference(),
    val roster: Roster = Roster(),
    val nextGameSchedule: GameScheduleData = GameScheduleData(),
    val previousGameSchedule: GameScheduleData = GameScheduleData(),
    val shortName: String = "",
    val officialSiteUrl: String = "",
)