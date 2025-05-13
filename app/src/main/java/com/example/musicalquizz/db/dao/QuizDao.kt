package com.example.musicalquizz.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicalquizz.db.entities.QuizEntity

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes")
    fun getAll(): LiveData<List<QuizEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(quiz: QuizEntity): Long

    @Delete
    suspend fun delete(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes WHERE id = :id")
    fun getById(id: Long): LiveData<QuizEntity?>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getEntityById(id: Long): QuizEntity?
}