package fi.tuni.tamk.tiko.myapplication


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Player(
    val person: Person = Person(),
    val jerseyNumber: String = "",
    val position: Position = Position()
)