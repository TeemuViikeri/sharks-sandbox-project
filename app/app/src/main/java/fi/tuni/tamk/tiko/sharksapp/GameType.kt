package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GameType(
    val id: String = "",
    val description: String = "",
    val postseason: Boolean = false
)