package com.appedia.runtracker.util

import android.view.View
import com.appedia.runtracker.services.ListOfPaths

fun View.gone(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun ListOfPaths.hasAtleastTwoPoints() = this.isNotEmpty() && this.last().size > 1

fun ListOfPaths.hasAtleastOnePoint() = this.isNotEmpty() && this.last().isNotEmpty()

fun ListOfPaths.getSecondLastPoint() = this.last()[this.last().size - 2]

fun ListOfPaths.getLastPoint() = this.last().last()