package org.example.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.example.app.R
import org.example.app.data.NotesRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 PUBLIC_INTERFACE
 Editor screen for adding or editing a note. Accepts optional EXTRA_NOTE_ID to edit.
 Returns to list after save or delete.
 */
class NoteEditorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }

    private val scope = MainScope()
    private lateinit var repository: NotesRepository

    private var noteId: Long? = null
    private lateinit var titleLayout: TextInputLayout
    private lateinit var titleInput: TextInputEditText
    private lateinit var contentLayout: TextInputLayout
    private lateinit var contentInput: TextInputEditText

    private var hasChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NotesRepository.getInstance(this)
        setContentView(R.layout.activity_note_editor)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleLayout = findViewById(R.id.title_layout)
        titleInput = findViewById(R.id.title_input)
        contentLayout = findViewById(R.id.content_layout)
        contentInput = findViewById(R.id.content_input)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1).takeIf { it > 0 }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { hasChanges = true }
            override fun afterTextChanged(s: Editable?) {}
        }
        titleInput.addTextChangedListener(watcher)
        contentInput.addTextChangedListener(watcher)

        if (noteId != null) {
            supportActionBar?.title = getString(R.string.edit_note)
            scope.launch {
                val note = repository.getNote(noteId!!)
                note?.let {
                    titleInput.setText(it.title)
                    contentInput.setText(it.content)
                    hasChanges = false
                }
            }
        } else {
            supportActionBar?.title = getString(R.string.add_note)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_note_editor, menu)
        // Show delete only when editing
        menu.findItem(R.id.action_delete).isVisible = noteId != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { handleBack(); true }
            R.id.action_save -> { saveNote(); true }
            R.id.action_delete -> { deleteNote(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = titleInput.text?.toString()?.trim().orEmpty()
        val content = contentInput.text?.toString()?.trim().orEmpty()

        if (title.isEmpty() && content.isEmpty()) {
            finish()
            return
        }

        scope.launch {
            if (noteId == null) {
                repository.addNote(title, content)
            } else {
                repository.updateNote(noteId!!, title, content)
            }
            hasChanges = false
            finish()
        }
    }

    private fun deleteNote() {
        val id = noteId ?: return
        scope.launch {
            repository.deleteNote(id)
            hasChanges = false
            finish()
        }
    }

    private fun handleBack() {
        if (hasChanges) {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.discard_changes))
                .setMessage(getString(R.string.discard_changes_message))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    finish()
                }.create()
            dialog.show()
        } else {
            finish()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Call super to satisfy lint's MissingSuperCall; then execute our custom back handling.
        super.onBackPressed()
        handleBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
