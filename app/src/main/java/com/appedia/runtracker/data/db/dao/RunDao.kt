package com.appedia.runtracker.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appedia.runtracker.data.db.entities.Run

@Dao
interface RunDao {

    /***
     * Insert a new Run in the database.
     * Replace old one with new one due to OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    /**
     * Delete a run from the database
     */
    @Delete
    suspend fun deleteRun(run: Run)

    /**
     * Get all the runs sorted DESCENDING by Date/Timestamp of the Run
     */
    @Query("SELECT * FROM run ORDER BY date_millis DESC")
    fun getAllRunsSortedByDate() : LiveData<List<Run>>

    /**
     * Get all the runs sorted DESCENDING by Average Speed of the Run
     */
    @Query("SELECT * FROM run ORDER BY average_speed_kmph DESC")
    fun getAllRunsSortedByAvgSpeedKMPH() : LiveData<List<Run>>

    /**
     * Get all the runs sorted DESCENDING by Run Distance
     */
    @Query("SELECT * FROM run ORDER BY distance_mtr DESC")
    fun getAllRunsSortedByDistanceMTR() : LiveData<List<Run>>

    /**
     * Get all the runs sorted DESCENDING by Run Duration
     */
    @Query("SELECT * FROM run ORDER BY duration_millis DESC")
    fun getAllRunsSortedByRunDuration() : LiveData<List<Run>>

    /**
     * Get all the runs sorted DESCENDING by calories burned
     */
    @Query("SELECT * FROM run ORDER BY calories_burned DESC")
    fun getAllRunsSortedByCaloriesBurned() : LiveData<List<Run>>

    /**
     * Get the total average speed across all runs
     */
    @Query("SELECT AVG(average_speed_kmph) FROM run")
    fun getTotalAvgSpeedKMPH() : LiveData<Float>

    /**
     * Get the total distance covered across all runs
     */
    @Query("SELECT SUM(distance_mtr) FROM run")
    fun getTotalDistanceMTR() : LiveData<Int>

    /**
     * Get total run duration across all runs
     */
    @Query("SELECT SUM(duration_millis) FROM run")
    fun getTotalRunDurationInMillis(): LiveData<Long>

    /**
     * Get total calories burned across all runs
     */
    @Query("SELECT SUM(calories_burned) FROM run")
    fun getTotalCaloriesBurned() : LiveData<Int>

}