package com.example.mycloset.data

import java.util.*

data class Outfit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val items: List<ClothingItem>,
    val modelImageUri: String? = null
)

// Extension functions for conversion
fun Outfit.toEntity(): OutfitEntity {
    return OutfitEntity(
        id = this.id,
        name = this.name,
        modelImageUri = this.modelImageUri
    )
}

fun OutfitEntity.toOutfit(items: List<ClothingItem>): Outfit {
    return Outfit(
        id = this.id,
        name = this.name,
        items = items,
        modelImageUri = this.modelImageUri
    )
}