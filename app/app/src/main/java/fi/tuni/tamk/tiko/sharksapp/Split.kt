package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Split(
    val season: String = "",
    val stat: Stat = Stat(),
    val date: String = ""
)