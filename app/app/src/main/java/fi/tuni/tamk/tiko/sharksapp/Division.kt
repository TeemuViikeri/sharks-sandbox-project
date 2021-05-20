package fi.tuni.tamk.tiko.sharksapp


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Division(
    val id: Int = 0,
    val name: String = "",
)