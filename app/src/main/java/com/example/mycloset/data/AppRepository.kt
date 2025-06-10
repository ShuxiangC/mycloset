package com.example.mycloset.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class AppRepository(private val database: AppDatabase) {
    private val clothingItemDao = database.clothingItemDao()
    private val outfitDao = database.outfitDao()
    private val categoryDao = database.categoryDao()

    // Initialize default categories
    suspend fun initializeDefaultCategories() {
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isEmpty()) {
            val defaultCategories = listOf("Tops", "Bottoms", "Dresses", "Shoes", "Accessories")
            defaultCategories.forEach { category ->
                categoryDao.insertCategory(CategoryEntity(category))
            }
        }
    }

    // Clothing Items
    fun getAllClothingItems(): Flow<List<ClothingItem>> {
        return clothingItemDao.getAllClothingItems().map { entities ->
            entities.map { it.toClothingItem() }
        }
    }

    fun getItemsByCategory(category: String): Flow<List<ClothingItem>> {
        return if (category == "All") {
            getAllClothingItems()
        } else {
            clothingItemDao.getItemsByCategory(category).map { entities ->
                entities.map { it.toClothingItem() }
            }
        }
    }

    suspend fun addClothingItem(item: ClothingItem) {
        clothingItemDao.insertClothingItem(item.toEntity())
    }

    suspend fun updateClothingItem(item: ClothingItem) {
        clothingItemDao.updateClothingItem(item.toEntity())
    }

    suspend fun removeClothingItem(item: ClothingItem) {
        clothingItemDao.deleteClothingItem(item.toEntity())
    }

    suspend fun getClothingItemById(id: String): ClothingItem? {
        return clothingItemDao.getClothingItemById(id)?.toClothingItem()
    }

    // Categories
    fun getAllCategories(): Flow<List<String>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.name }
        }
    }

    fun getCategoryCounts(): Flow<List<CategoryCount>> {
        return clothingItemDao.getCategoryCounts()
    }

    suspend fun addCategory(category: String) {
        categoryDao.insertCategory(CategoryEntity(category))
    }

    suspend fun removeCategory(category: String) {
        categoryDao.deleteCategory(CategoryEntity(category))
    }

    suspend fun updateCategory(oldCategory: String, newCategory: String) {
        categoryDao.updateItemsCategory(oldCategory, newCategory)
        categoryDao.deleteCategory(CategoryEntity(oldCategory))
        categoryDao.insertCategory(CategoryEntity(newCategory))
    }

    // Outfits
    fun getAllOutfits(): Flow<List<Outfit>> {
        return outfitDao.getAllOutfits().map { outfitEntities ->
            outfitEntities.map { outfitEntity ->
                val outfitItems = outfitDao.getOutfitItems(outfitEntity.id)
                val clothingItems = outfitItems.mapNotNull { outfitItem ->
                    clothingItemDao.getClothingItemById(outfitItem.clothingItemId)?.toClothingItem()
                }
                outfitEntity.toOutfit(clothingItems)
            }
        }
    }

    suspend fun addOutfit(outfit: Outfit) {
        outfitDao.insertOutfit(outfit.toEntity())
        val outfitItems = outfit.items.map { item ->
            OutfitItemEntity(
                id = UUID.randomUUID().toString(),
                outfitId = outfit.id,
                clothingItemId = item.id
            )
        }
        outfitDao.insertOutfitItems(outfitItems)
    }

    suspend fun updateOutfit(outfit: Outfit) {
        outfitDao.updateOutfit(outfit.toEntity())
        outfitDao.deleteOutfitItems(outfit.id)
        val outfitItems = outfit.items.map { item ->
            OutfitItemEntity(
                id = UUID.randomUUID().toString(),
                outfitId = outfit.id,
                clothingItemId = item.id
            )
        }
        outfitDao.insertOutfitItems(outfitItems)
    }

    suspend fun removeOutfit(outfit: Outfit) {
        outfitDao.deleteOutfit(outfit.toEntity())
    }

    suspend fun getOutfitById(id: String): Outfit? {
        val outfitEntity = outfitDao.getOutfitById(id) ?: return null
        val outfitItems = outfitDao.getOutfitItems(id)
        val clothingItems = outfitItems.mapNotNull { outfitItem ->
            clothingItemDao.getClothingItemById(outfitItem.clothingItemId)?.toClothingItem()
        }
        return outfitEntity.toOutfit(clothingItems)
    }
}