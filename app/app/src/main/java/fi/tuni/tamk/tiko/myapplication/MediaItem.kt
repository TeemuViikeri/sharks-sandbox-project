package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MediaItem(
    val blurb: String = "",
    val playbacks: List<Playback> = listOf()
)
