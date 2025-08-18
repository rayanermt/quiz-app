package com.pdm.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pdm.quiz.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY name")
    suspend fun getAll(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CategoryEntity>)
}
