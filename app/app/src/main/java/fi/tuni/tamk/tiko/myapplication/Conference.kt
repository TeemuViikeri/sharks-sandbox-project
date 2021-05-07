package fi.tuni.tamk.tiko.myapplication


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Conference(
    val id: Int = 0,
    val name: String = "",
)