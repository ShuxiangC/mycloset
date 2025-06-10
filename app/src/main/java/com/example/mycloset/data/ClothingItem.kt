package com.example.mycloset.data

import java.util.*

data class ClothingItem(
    val id: String = UUID.randomUUID().toString(),
    val imageUri: String,
    val category: String,
    val name: String = ""
)

// Extension functions for conversion
fun ClothingItem.toEntity(): ClothingItemEntity {
    return ClothingItemEntity(
        id = this.id,
        imageUri = this.imageUri,
        category = this.category,
        name = this.name
    )
}

fun ClothingItemEntity.toClothingItem(): ClothingItem {
    return ClothingItem(
        id = this.id,
        imageUri = this.imageUri,
        category = this.category,
        name = this.name
    )
}