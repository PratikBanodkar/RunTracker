package com.appedia.runtracker.data.db.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run")
data class Run (

        @ColumnInfo(name = "image")
        var image : Bitmap? = null,

        @ColumnInfo(name = "date_millis")
        var runDateInMillis : Long = 0L,

        @ColumnInfo(name = "average_speed_kmph")
        var avgSpeedKMPH : Float = 0F,

        @ColumnInfo(name = "distance_mtr")
        var distanceMTR : Int = 0,

        @ColumnInfo(name = "duration_millis")
        var runDurationInMillis : Long = 0L,

        @ColumnInfo(name = "calories_burned")
        var caloriesBurned : Int = 0

){

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

}

