package com.example.mystoryapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Story(

    @PrimaryKey
    @ColumnInfo
    var id: String,

    @ColumnInfo
    var name: String? = null,

    @ColumnInfo
    var description: String? = null,

    @ColumnInfo
    var photoUrl: String? = null,

    @ColumnInfo
    var createdAt: String? = null
)