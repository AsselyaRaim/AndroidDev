package com.example.cryptotracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptotracker.data.models.currency.Currency

@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var login: String? = null,
    var name: String? = null,
    var password: String? = null,
    var favorites: MutableList<Int>? = null
)
