package com.drivenote.app.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class DriveNoteCarSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen = DriveNoteCarScreen(carContext)
}
