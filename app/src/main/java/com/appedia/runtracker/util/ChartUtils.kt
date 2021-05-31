package com.appedia.runtracker.util

import android.content.Context
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.appedia.runtracker.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

object ChartUtils {

    fun generateLineDataSet(
        chartOptionType: ChartOptionType,
        dataValues: List<Entry>,
        context: Context
    ): LineDataSet {
        when (chartOptionType) {
            ChartOptionType.AVERAGE_SPEED -> {
                return LineDataSet(dataValues, "Average Speed Data Set").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    valueTextColor = Color.WHITE
                    color = getColor(R.color.avg_speed_color)
                    valueTextSize = 15f
                    fillDrawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.gradient_avg_speed, null
                    )
                    setDrawFilled(true)
                }
            }
            ChartOptionType.RUNNING_TIME -> {
                return LineDataSet(dataValues, "Running Time Data Set").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    valueTextColor = Color.WHITE
                    color = getColor(R.color.run_time_color)
                    valueTextSize = 15f
                    fillDrawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.gradient_run_time, null
                    )
                    setDrawFilled(true)
                }
            }
            ChartOptionType.DISTANCE -> {
                return LineDataSet(dataValues, "Running Distance Data Set").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    valueTextColor = Color.WHITE
                    color = getColor(R.color.run_distance_color)
                    valueTextSize = 15f
                    fillDrawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.gradient_run_distance, null
                    )
                    setDrawFilled(true)
                }
            }
            ChartOptionType.CALORIES_BURNED -> {
                return LineDataSet(dataValues, "Calories Burned Data Set").apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    valueTextColor = Color.WHITE
                    color = getColor(R.color.calories_burned_color)
                    valueTextSize = 15f
                    fillDrawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.gradient_calories_burned, null
                    )
                    setDrawFilled(true)
                }
            }
        }
    }


}