package org.example.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * PUBLIC_INTERFACE
 * Room database that stores notes.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // PUBLIC_INTERFACE
        fun getInstance(context: Context): AppDatabase {
            val current = INSTANCE
            if (current != null) return current
            synchronized(this) {
                val created = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ocean_notes.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = created
                return created
            }
        }
    }
}
