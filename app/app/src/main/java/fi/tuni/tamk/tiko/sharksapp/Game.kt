package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Game(
    val gamePk: Int = 0,
    val link: String = "",
    val gameDate: String = "",
    val teams: GameDayTeams = GameDayTeams(),
    val venue: GameDayVenue = GameDayVenue(),
    val content: Content = Content(),
)