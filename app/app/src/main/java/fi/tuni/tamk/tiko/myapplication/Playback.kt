package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Playback(
    val width: String = "",
    val height: String = "",
    val url: String = ""
)
