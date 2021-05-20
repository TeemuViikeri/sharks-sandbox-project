package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Season(
    val seasonId: String = "",
    val regularSeasonStartDate: String = "",
    val regularSeasonEndDate: String = "",
    val seasonEndDate: String = "",
    val numberOfGames: Int = 0
)
