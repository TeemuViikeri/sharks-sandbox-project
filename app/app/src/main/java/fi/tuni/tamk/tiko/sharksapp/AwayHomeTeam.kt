package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AwayHomeTeam(
    val id: Int = 0,
    val name: String = "",
    val link: String = ""
)
