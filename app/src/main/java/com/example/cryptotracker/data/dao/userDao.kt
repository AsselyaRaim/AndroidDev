package com.example.cryptotracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cryptotracker.data.models.User

@Dao
interface userDao {
    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM Users")
    fun getAll(): List<User>?

    @Query("SELECT * FROM Users WHERE id=:userId")
    fun getUser(userId: Long): User?

    @Query("SELECT * FROM Users WHERE login=:userLogin AND password=:userPassword")
    fun findUser(userLogin: String, userPassword: String): User?

    @Query("SELECT * FROM Users WHERE login=:userLogin")
    fun findUserByLogin(userLogin: String): User?

    @Query("UPDATE Users SET favorites=:newFavorites WHERE id=:userId")
    fun updateFavorites(userId: Long, newFavorites: MutableList<Int>?)

}