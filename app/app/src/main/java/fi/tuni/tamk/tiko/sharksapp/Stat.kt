package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Stat(
    val timeOnIce: String = "",
    val assists: Int = 0,
    val goals: Int = 0,
    val pim: Int = 0,
    val shots: Int = 0,
    val games: Int = 0,
    val hits: Int = 0,
    val powerPlayGoals: Int = 0,
    val powerPlayPoints: Int = 0,
    val powerPlayTimeOnIce: String = "",
    val evenTimeOnIce: String = "",
    val penaltyMinutes: String = "",
    val faceOffPct: Int = 0,
    val shotPct: Int = 0,
    val gameWinningGoals: Int = 0,
    val overTimeGoals: Int = 0,
    val shortHandedGoals: Int = 0,
    val shortHandedPoints: Int = 0,
    val shortHandedTimeOnIce: String = "",
    val blocked: Int = 0,
    val plusMinus: Int = 0,
    val points: Int = 0,
    val shifts: Int = 0,
    val timeOnIcePerGame: String = "",
    val evenTimeOnIcePerGame: String = "",
    val shortHandedTimeOnIcePerGame: String = "",
    val powerPlayTimeOnIcePerGame: String = "",
)
