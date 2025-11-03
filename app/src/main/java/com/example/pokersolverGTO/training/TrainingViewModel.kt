package com.example.pokersolverGTO.training

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokersolverGTO.data.AppDatabase
import com.example.pokersolverGTO.data.TrainingStats
import com.example.pokersolverGTO.engine.SolverEngine
import com.example.pokersolverGTO.repository.DatabaseRepository
import com.example.pokersolverGTO.repository.GTOStrategyResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainingViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    private val statsDao = db.trainingStatsDao()
    private val repo = DatabaseRepository(app)
    private val engine = SolverEngine(repo)

    private val _currentScenario = MutableStateFlow<GTOStrategyResult?>(null)
    val currentScenario: StateFlow<GTOStrategyResult?> = _currentScenario.asStateFlow()

    private val _stats = MutableStateFlow(TrainingStats())
    val stats: StateFlow<TrainingStats> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            statsDao.get()?.let { _stats.value = it } ?: statsDao.insert(TrainingStats())
            loadRandomScenario()
        }
    }

    fun loadRandomScenario() {
        viewModelScope.launch {
            val s = repo.getRandomScenario()
            _currentScenario.value = s
        }
    }

    fun submitAnswer(action: String) {
        viewModelScope.launch {
            val s = _currentScenario.value ?: return@launch
            val best = s.gto_strategy.maxByOrNull { it.value }?.key ?: "check"
            val correct = action == best
            val updated = _stats.value.copy(
                totalQuestions = _stats.value.totalQuestions + 1,
                correctAnswers = _stats.value.correctAnswers + if (correct) 1 else 0,
                lastTimestamp = System.currentTimeMillis()
            )
            statsDao.insert(updated)
            _stats.value = updated
            loadRandomScenario()
        }
    }
}

