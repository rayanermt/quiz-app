package com.pdm.quiz.presentation.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pdm.quiz.R
import com.pdm.quiz.app.QuizApp
import com.pdm.quiz.databinding.ActivityQuizBinding
import com.pdm.quiz.databinding.ScoreDialogBinding
import kotlinx.coroutines.launch
import kotlin.math.floor

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: QuizViewModel
    private var selectedIndex: Int = -1

    private val uiHandler = Handler(Looper.getMainLooper())
    private var uiTimerStartMs: Long = 0L
    private var uiTimerRunning = false

    private val uiTimerTick = object : Runnable {
        override fun run() {
            val elapsed = System.currentTimeMillis() - uiTimerStartMs
            binding.timerIndicatorTextview.text = formatElapsed(elapsed)
            uiHandler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val quizId = intent.getStringExtra("quizId") ?: ""
        val quizName = intent.getStringExtra("quizName") ?: getString(R.string.app_name)

        val app = application as QuizApp
        val factory = QuizViewModelFactory(
            app.container.questionsRepository,
            app.container.sessionsRepository
        )
        viewModel = ViewModelProvider(this, factory)[QuizViewModel::class.java]

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)

        binding.nextBtn.setOnClickListener {
            if (selectedIndex == -1) {
                Toast.makeText(this, "Selecione uma alternativa.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.answer(selectedIndex)
                selectedIndex = -1
                clearSelection()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    // Loading
                    binding.questionProgressIndicator.isIndeterminate = ui.loading
                    binding.nextBtn.isEnabled = !ui.loading
                    binding.nextBtn.text = if (ui.index + 1 >= ui.total && !ui.loading) {
                        "Finalizar"
                    } else {
                        "Próximo"
                    }

                    if (ui.error != null) {
                        Toast.makeText(this@QuizActivity, ui.error, Toast.LENGTH_SHORT).show()
                    }

                    if (!ui.loading && ui.total > 0) {
                        binding.questionProgressIndicator.isIndeterminate = false
                        binding.questionProgressIndicator.max = ui.total
                        binding.questionProgressIndicator.setProgress(ui.index, true)
                        binding.questionIndicatorTextview.text = "Pergunta ${ui.index + 1}/${ui.total}"
                    }

                    ui.question?.let { q ->
                        binding.questionTextview.text = q.text
                        binding.btn0.text = q.options.getOrNull(0) ?: ""
                        binding.btn1.text = q.options.getOrNull(1) ?: ""
                        binding.btn2.text = q.options.getOrNull(2) ?: ""
                        binding.btn3.text = q.options.getOrNull(3) ?: ""
                        binding.btn0.isVisible = q.options.size > 0
                        binding.btn1.isVisible = q.options.size > 1
                        binding.btn2.isVisible = q.options.size > 2
                        binding.btn3.isVisible = q.options.size > 3
                    }

                    if (ui.finished) {
                        stopUiTimer()
                        showScoreDialog(score = ui.score, total = ui.total, quizName = ui.quizName)
                    } else if (!ui.loading && !uiTimerRunning) {
                        startUiTimer()
                    }
                }
            }
        }

        viewModel.init(quizId, quizName)
    }

    override fun onClick(v: View?) {
        selectedIndex = when (v?.id) {
            binding.btn0.id -> 0
            binding.btn1.id -> 1
            binding.btn2.id -> 2
            binding.btn3.id -> 3
            else -> -1
        }
        updateSelectionUI()
    }

    private fun updateSelectionUI() {
        val all = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
        all.forEachIndexed { idx, button ->
            setSelectedStyle(button, isSelected = (idx == selectedIndex))
        }
    }

    private fun clearSelection() {
        val all = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
        all.forEach { setSelectedStyle(it, isSelected = false) }
    }

    private fun setSelectedStyle(btn: Button, isSelected: Boolean) {
        val colorRes = if (isSelected) R.color.purple_200 else R.color.grey
        btn.backgroundTintList = getColorStateList(colorRes)
    }

    private fun startUiTimer() {
        uiTimerStartMs = System.currentTimeMillis()
        uiTimerRunning = true
        uiHandler.post(uiTimerTick)
    }

    private fun stopUiTimer() {
        uiTimerRunning = false
        uiHandler.removeCallbacks(uiTimerTick)
    }

    private fun formatElapsed(ms: Long): String {
        val totalSec = floor(ms / 1000.0).toLong()
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%d:%02d", min, sec)
    }

    private fun showScoreDialog(score: Int, total: Int, quizName: String) {
        val percentage = if (total > 0) ((score.toFloat() / total.toFloat()) * 100).toInt() else 0

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater).apply {
            try {
                scoreProgressIndicator.setProgress(percentage, true)
            } catch (_: Throwable) {
                scoreProgressIndicator.progress = percentage
            }

            scoreProgressText.text = "$percentage %"

            val passed = percentage >= 60
            scoreTitle.text = if (passed) "Parabéns! Você passou no quiz 🎉"
            else "Opa, você não passou no quiz 😓"
            scoreTitle.setTextColor(
                ContextCompat.getColor(
                    this@QuizActivity,
                    if (passed) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )

            scoreSubtitle.text = "$score de $total perguntas corretas • $quizName"

        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.finishBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUiTimer()
    }
}
