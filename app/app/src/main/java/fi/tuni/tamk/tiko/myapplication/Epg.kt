package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Epg(
    val title: String = "",
    val items: List<MediaItem> = listOf()
)
