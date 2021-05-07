package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GameDayTeams(
    val away: GameDayTeam = GameDayTeam(),
    val home: GameDayTeam = GameDayTeam()
)