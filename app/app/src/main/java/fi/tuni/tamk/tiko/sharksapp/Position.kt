package fi.tuni.tamk.tiko.sharksapp


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Position(
    val code: String = "",
    val name: String = "",
    val type: String = "",
    val abbreviation: String = ""
)