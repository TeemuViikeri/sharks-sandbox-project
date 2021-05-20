package fi.tuni.tamk.tiko.sharksapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContentData(
    val copyright: String = "",
    val link: String = "",
    val media: Media = Media(),
)