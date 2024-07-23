package com.example.dicodingstory.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    @ColumnInfo("id")
    var id  : String,

    @ColumnInfo(name="photoUrl")
    var photoUrl : String,

    @ColumnInfo(name="name")
    var name : String,

    @ColumnInfo(name = "description")
    var description : String,

    @ColumnInfo(name="lat")
    var lat : Double? = null,

    @ColumnInfo(name = "lon")
    var lon:Double?=null
)