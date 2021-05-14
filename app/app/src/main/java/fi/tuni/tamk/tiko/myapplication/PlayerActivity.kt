package fi.tuni.tamk.tiko.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper

class PlayerActivity : AppCompatActivity() {

    /**
     * Base URL for the NHL API that will be expanded by endpoints.
     */
    private val baseURL: String = "https://statsapi.web.nhl.com"

    /**
     * Title text view for the activity. Displays a player's name.
     */
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        title = findViewById(R.id.tvPlayerTitle)

        val intent = intent
        val playerLink = intent.getStringExtra("url").toString()
        val playerURL = "$baseURL$playerLink"

        val http = HttpConnection()

        http.fetchAsync(playerURL, this) {
            val player = convertJsonToPlayerObject(it)
            title.text = player.fullName
            val number: TextView = findViewById(R.id.tvPlayerInfoNumber)
            number.text = player.primaryNumber
            val age: TextView = findViewById(R.id.tvPlayerInfoAge)
            age.text = player.currentAge.toString()
            val nationality: TextView = findViewById(R.id.tvPlayerInfoNationality)
            nationality.text = player.nationality
            val height: TextView = findViewById(R.id.tvPlayerInfoHeight)
            height.text = player.height
            val weight: TextView = findViewById(R.id.tvPlayerInfoWeight)
            weight.text = "${player.weight} lbs"
            val shoots: TextView = findViewById(R.id.tvPlayerInfoShoots)
            shoots.text = player.shootsCatches
            val primaryPosition: TextView = findViewById(R.id.tvPlayerInfoPosition)
            primaryPosition.text = player.primaryPosition.name
        }
    }

    /**
     * Converts JSON data of a player from NHL API to Jackson mapped Player object.
     *
     * @param json Returned JSON data from the NHL API's player endpoint.
     * @return Converted Player object.
     */
    private fun convertJsonToPlayerObject(json: String): People {
        val mp = ObjectMapper()
        val playerData: PeopleData = mp.readValue(json, PeopleData::class.java)
        return playerData.people[0]
    }

}
