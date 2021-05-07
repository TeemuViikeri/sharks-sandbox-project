package fi.tuni.tamk.tiko.myapplication


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Person(
    val id: Int = 0,
    val fullName: String = "",
    val link: String = ""
)