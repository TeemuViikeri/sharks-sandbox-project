package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TeamData(
    val copyright: String = "",
    val teams: List<Team> = listOf()
)