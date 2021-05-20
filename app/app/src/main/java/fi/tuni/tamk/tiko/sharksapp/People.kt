package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class People(
    val id: Int = 0,
    val fullName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val primaryNumber: String = "",
    val birthDate: String = "",
    val currentAge: Int = 0,
    val nationality: String = "",
    val height: String = "",
    val weight: Int = 0,
    val active: Boolean = true,
    val alternateCaptain: Boolean = true,
    val rookie: Boolean = true,
    val shootsCatches: String = "",
    val rosterStatus: String = "",
    val currentTeam: SmallTeamData = SmallTeamData(),
    val primaryPosition: Position = Position()
)