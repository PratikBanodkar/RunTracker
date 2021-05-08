package com.appedia.runtracker.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.appedia.runtracker.data.db.dao.RunDao
import com.appedia.runtracker.data.db.entities.Run
import com.appedia.runtracker.data.db.type_converters.Converters

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunTrackerDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDao

}