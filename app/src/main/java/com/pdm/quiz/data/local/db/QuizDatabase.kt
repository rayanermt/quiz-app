package com.pdm.quiz.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pdm.quiz.data.local.dao.CategoryDao
import com.pdm.quiz.data.local.dao.QuestionDao
import com.pdm.quiz.data.local.dao.SessionDao
import com.pdm.quiz.data.local.entity.CategoryEntity
import com.pdm.quiz.data.local.entity.QuestionEntity
import com.pdm.quiz.data.local.entity.QuizSessionEntity

@Database(
    entities = [CategoryEntity::class, QuestionEntity::class, QuizSessionEntity::class],
    version = 2
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun questionDao(): QuestionDao
    abstract fun sessionDao(): SessionDao
}
