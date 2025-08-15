package com.pdm.quiz.ScoreActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pdm.quiz.MainActivity
import com.pdm.quiz.R

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.grey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val score = intent.getIntExtra("Score", 0)

        setContent {
            ScoreScreen(score = score) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}