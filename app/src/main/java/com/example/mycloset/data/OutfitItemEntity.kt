package com.example.mycloset.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "outfit_items",
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntity::class,
            parentColumns = ["id"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["clothingItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OutfitItemEntity(
    @PrimaryKey val id: String,
    val outfitId: String,
    val clothingItemId: String
)
