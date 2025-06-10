package com.example.mycloset.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val modelImageUri: String?
)