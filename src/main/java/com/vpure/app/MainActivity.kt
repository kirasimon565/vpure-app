package com.vpure.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vpure.app.data.Preferences

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if a token exists
        if (Preferences.getToken(this) != null) {
            // User is logged in, go to Home
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // User is not logged in, go to Login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Finish this activity so the user can't navigate back to it
    }
}
