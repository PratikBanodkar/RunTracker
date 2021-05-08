package com.appedia.runtracker.data.db.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run")
data class Run (

        @ColumnInfo(name = "image")
        private var image : Bitmap? = null,

        @ColumnInfo(name = "date_millis")
        private var runDateInMillis : Long = 0L,

        @ColumnInfo(name = "average_speed_kmph")
        private var avgSpeedKMPH : Float = 0F,

        @ColumnInfo(name = "distance_mtr")
        private var distanceMTR : Int = 0,

        @ColumnInfo(name = "duration_millis")
        private var runDurationInMillis : Long = 0L,

        @ColumnInfo(name = "calories_burned")
        private var caloriesBurned : Int = 0

){

    @PrimaryKey(autoGenerate = true)
    private var id : Int = 0

}

