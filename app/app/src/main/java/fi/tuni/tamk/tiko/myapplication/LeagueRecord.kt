package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class LeagueRecord(
    val wins: Int = 0,
    val losses: Int = 0,
    val ot: Int = 0,
)