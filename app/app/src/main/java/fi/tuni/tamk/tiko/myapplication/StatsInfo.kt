package fi.tuni.tamk.tiko.myapplication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class StatsInfo(
    val type: StatsType = StatsType(),
    val splits: List<Split> = listOf()
)
