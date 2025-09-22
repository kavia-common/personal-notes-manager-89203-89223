package org.example.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.View
import android.widget.TextView
import org.example.app.R
import org.example.app.data.NotesRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.example.app.data.Note

/**
 PUBLIC_INTERFACE
 Main screen listing notes with a floating action button to create new notes.
 */
class NotesListActivity : AppCompatActivity() {

    private val scope = MainScope()
    private lateinit var repository: NotesRepository
    private lateinit var recycler: RecyclerView
    private lateinit var empty: TextView
    private val adapter = NotesAdapter(
        onClick = { note -> openEditor(note.id) },
        onLongClick = { note -> confirmDelete(note) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NotesRepository.getInstance(this)
        setContentView(R.layout.activity_notes_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.notes_list_title)

        recycler = findViewById(R.id.recycler)
        empty = findViewById(R.id.empty_text)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            openEditor(null)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun openEditor(id: Long?) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        if (id != null) intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, id)
        startActivity(intent)
    }

    private fun confirmDelete(note: Note) {
        // Simple immediate delete on long-press for now (clean UX). Could add dialog if desired.
        scope.launch {
            repository.deleteNote(note.id)
            refresh()
        }
    }

    private fun refresh() {
        scope.launch {
            val notes = repository.getNotes()
            adapter.submit(notes)
            empty.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
