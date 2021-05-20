package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GameDayTeam(
    val leagueRecord: LeagueRecord = LeagueRecord(),
    val score: Int = 0,
    val team: AwayHomeTeam = AwayHomeTeam()
)