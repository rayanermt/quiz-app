package com.pdm.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pdm.quiz.data.local.entity.QuestionEntity

@Dao
interface QuestionDao {
    @Query("SELECT * FROM question WHERE categoryId = :categoryId")
    suspend fun byCategory(categoryId: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM question WHERE categoryId = :categoryId")
    suspend fun countByCategory(categoryId: String): Int
}
