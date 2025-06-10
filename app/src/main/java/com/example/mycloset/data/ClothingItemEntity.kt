package com.example.mycloset.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey val id: String,
    val imageUri: String,
    val category: String,
    val name: String
)