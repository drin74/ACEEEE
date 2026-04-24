package com.example.a2ace


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a2ace.Link
import com.google.android.material.card.MaterialCardView


class LinkAdapter(
    private val links: List<Link>,
    private val onItemClick: (Link) -> Unit
) : RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {


    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.cardLink)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val ivIndicator: ImageView = itemView.findViewById(R.id.ivIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_link, parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = links[position]


        holder.tvTitle.text = link.title

        holder.tvId.text = "ID: ${link.aceStreamId.take(20)}${if (link.aceStreamId.length > 20) "..." else ""}"


        if (link.iconResId != 0) {
            holder.ivIcon.setImageResource(link.iconResId)
            holder.ivIcon.visibility = View.VISIBLE
        } else {
            holder.ivIcon.visibility = View.GONE
        }


        holder.card.setOnClickListener {

            holder.card.isPressed = true
            holder.card.postDelayed({ holder.card.isPressed = false }, 100)

            onItemClick(link)
        }


        holder.ivIndicator.setImageResource(R.drawable.ic_circle_green)
    }

    override fun getItemCount(): Int = links.size
}