package com.appedia.runtracker.data.repositories

import com.appedia.runtracker.data.db.dao.RunDao
import com.appedia.runtracker.data.db.entities.Run
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dao: RunDao
    ) {

    suspend fun insertRun(run : Run) = dao.insertRun(run)

    suspend fun deleteRun(run : Run) = dao.deleteRun(run)

    fun getAllRunsSortedByDate() = dao.getAllRunsSortedByDate()

    fun getAllRunsSortedByAvgSpeedKMPH() = dao.getAllRunsSortedByAvgSpeedKMPH()

    fun getAllRunsSortedByDistanceMTR() = dao.getAllRunsSortedByDistanceMTR()

    fun getAllRunsSortedByRunDuration() = dao.getAllRunsSortedByRunDuration()

    fun getAllRunsSortedByCaloriesBurned() = dao.getAllRunsSortedByCaloriesBurned()

    fun getTotalAvgSpeedKMPH() = dao.getTotalAvgSpeedKMPH()

    fun getTotalDistanceMTR() = dao.getTotalDistanceMTR()

    fun getTotalRunDurationInMillis() = dao.getTotalRunDurationInMillis()

    fun getTotalCaloriesBurned() = dao.getTotalCaloriesBurned()

    fun getAllRunTimesList() = dao.getAllRunTimesList()

    fun getAllCaloriesBurnedList() = dao.getAllCaloriesBurnedList()

    fun getAllRunningDistancesList() = dao.getAllRunningDistancesList()

    fun getAllAverageSpeedsList() = dao.getAllAverageSpeedsList()
}