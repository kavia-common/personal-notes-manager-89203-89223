package org.example.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.Note
import java.text.DateFormat
import java.util.Date

/**
 * RecyclerView adapter for notes list.
 */
class NotesAdapter(
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.Holder>() {

    private val items: MutableList<Note> = mutableListOf()

    fun submit(list: List<Note>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note_card, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.bind(item, onClick, onLongClick)
    }

    override fun getItemCount(): Int = items.size

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.note_title)
        private val content: TextView = itemView.findViewById(R.id.note_content)
        private val subtitle: TextView = itemView.findViewById(R.id.note_subtitle)
        private val card: CardView = itemView.findViewById(R.id.note_card)

        fun bind(note: Note, onClick: (Note) -> Unit, onLongClick: (Note) -> Unit) {
            title.text = if (note.title.isNotBlank()) note.title else itemView.context.getString(R.string.title_hint)
            content.text = note.content
            val dateStr = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(Date(note.updatedAt))
            subtitle.text = itemView.context.getString(R.string.last_edited, dateStr)

            card.setOnClickListener { onClick(note) }
            card.setOnLongClickListener {
                onLongClick(note)
                true
            }
        }
    }
}
