package com.pdm.quiz.data.mapper

import com.google.gson.Gson
import com.pdm.quiz.data.local.entity.CategoryEntity
import com.pdm.quiz.data.local.entity.QuestionEntity
import com.pdm.quiz.data.remote.dto.CategoryDto
import com.pdm.quiz.data.remote.dto.QuestionDto
import com.pdm.quiz.domain.model.Category
import com.pdm.quiz.domain.model.Question

private val gson = Gson()

fun CategoryEntity.toDomain() = Category(id, name)
fun QuestionEntity.toDomain(): Question =
    Question(id, categoryId, text, gson.fromJson(optionsJson, Array<String>::class.java).toList(), correctIndex)

fun CategoryDto.toEntity() = CategoryEntity(id = id, name = name)
fun QuestionDto.toEntity(categoryId: String) = QuestionEntity(
    id = id,
    categoryId = categoryId,
    text = text,
    optionsJson = gson.toJson(options),
    correctIndex = correctIndex,
    updatedAt = updatedAt
)
