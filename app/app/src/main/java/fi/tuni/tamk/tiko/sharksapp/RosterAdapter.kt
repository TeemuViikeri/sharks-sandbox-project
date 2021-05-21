package fi.tuni.tamk.tiko.sharksapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RosterAdapter(
    private var players: List<Player>,
    private val context: Context
) : RecyclerView.Adapter<RosterAdapter.RosterViewHolder>() {

    /**
     * ViewHolder for roster which holds player's number, name and position.
     */
    inner class RosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlayerNumber: TextView = view.findViewById(R.id.tvPlayerNumber)
        val tvPlayerName: TextView = view.findViewById(R.id.tvPlayerName)
        val tvPlayerPosition: TextView = view.findViewById(R.id.tvPlayerPosition)
    }

    /**
     * Called to create the ViewHolder that will include Views given in the
     * LayoutInflater.
     *
     * @param parent Parent ViewGroup which will be inflated with ViewHolder.
     * @param viewType Type of the view to be inflated to .
     * @return ViewHolder that holds the view(s).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        return RosterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.player,
                parent,
                false
            )
        )
    }

    /**
     * Binds view to ViewHolder and displays the data at a certain position.
     *
     * @param holder The ViewHolder which updates the items at the given
     * position.
     * @param position The position of the item in the dataset given to the
     * adapter.
     */
    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        val player: Player = players[position]
        holder.tvPlayerNumber.text = player.jerseyNumber
        holder.tvPlayerName.text = player.person.fullName
        holder.tvPlayerPosition.text = player.position.abbreviation

        holder.tvPlayerName.isClickable = true
        holder.tvPlayerName.setOnClickListener {
            val playerIntent = Intent(context, PlayerActivity::class.java).apply {
                putExtra("url", player.person.link)
            }
            context.startActivity(playerIntent)
        }
    }

    /**
     * Return item count of the dataset given to this adapter.
     *
     * @return Item count.
     */
    override fun getItemCount(): Int {
        return players.size
    }

    /**
     * Sorts a list of Player objects numerically by their jersey number
     * property.
     *
     * @return Sorted list of Players.
     */
    fun sortPlayersByJerseyNumber() {
        this.players = players.sortedBy { it.jerseyNumber.toInt() }
        notifyDataSetChanged()
    }

    /**
     * Sorts a list of Player objects alphabetically by their name property.
     *
     * @return Sorted list of Players.
     */
    fun sortPlayersByName() {
        this.players = players.sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER, { it.person.fullName }))
        notifyDataSetChanged()
    }

    /**
     * Sorts a list of Player objects alphabetically by their position
     * property.
     *
     * @return Sorted list of Players.
     */
    fun sortPlayersByPosition(){
        this.players = players.sortedBy { it.position.toString() }
        notifyDataSetChanged()
    }
}