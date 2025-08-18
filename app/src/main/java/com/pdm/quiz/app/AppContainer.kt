package com.pdm.quiz.app

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.quiz.data.local.db.QuizDatabase
import com.pdm.quiz.data.remote.auth.FirebaseAuthDataSource
import com.pdm.quiz.data.remote.firestore.QuestionsRemoteDataSource
import com.pdm.quiz.data.remote.firestore.SessionsRemoteDataSource
import com.pdm.quiz.data.remote.firestore.UsersRemoteDataSource
import com.pdm.quiz.data.repository.impl.AuthRepositoryImpl
import com.pdm.quiz.data.repository.impl.QuestionsRepositoryImpl
import com.pdm.quiz.data.repository.impl.SessionsRepositoryImpl
import com.pdm.quiz.data.repository.impl.UsersRepositoryImpl
import com.pdm.quiz.domain.repository.AuthRepository
import com.pdm.quiz.domain.repository.QuestionsRepository
import com.pdm.quiz.domain.repository.SessionsRepository
import com.pdm.quiz.domain.repository.UsersRepository

class AppContainer(context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val db = Room.databaseBuilder(context, QuizDatabase::class.java, "quiz.db")
        .fallbackToDestructiveMigration()
        .build()

    // DataSources
    private val authDs = FirebaseAuthDataSource(auth)
    private val usersRemote = UsersRemoteDataSource(firestore)
    private val qRemote = QuestionsRemoteDataSource(firestore)
    private val sRemote = SessionsRemoteDataSource(firestore)

    // Repos
    val usersRepository: UsersRepository = UsersRepositoryImpl(usersRemote)
    val authRepository: AuthRepository = AuthRepositoryImpl(authDs, usersRepository)
    val questionsRepository: QuestionsRepository = QuestionsRepositoryImpl(qRemote, db.categoryDao(), db.questionDao())
    val sessionsRepository: SessionsRepository = SessionsRepositoryImpl(auth, db.sessionDao(), sRemote)
}
