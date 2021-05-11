package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SmallTeamData(
    val id: Int = 0,
    val name: String = "",
    val link: String = "",
)
