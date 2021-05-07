package fi.tuni.tamk.tiko.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RosterAdapter(
    private val players: List<Player>
) : RecyclerView.Adapter<RosterAdapter.RosterViewHolder>() {

    inner class RosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlayerNumber: TextView = view.findViewById(R.id.tvPlayerNumber)
        val tvPlayerName: TextView = view.findViewById(R.id.tvPlayerName)
        val tvPlayerPosition: TextView = view.findViewById(R.id.tvPlayerPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        return RosterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.player,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        val player: Player = players[position]
        holder.tvPlayerNumber.text = player.jerseyNumber
        holder.tvPlayerName.text = player.person.fullName
        holder.tvPlayerPosition.text = player.position.abbreviation
    }

    override fun getItemCount(): Int {
        return players.size
    }
}