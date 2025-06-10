package com.example.mycloset.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.example.mycloset.utils.ImageUtils
import java.util.*

class AppRepository(
    private val database: AppDatabase,
    private val context: Context // Add context parameter
) {
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

    suspend fun addClothingItem(item: ClothingItem): Boolean {
        return try {
            // Copy image to internal storage if it's a content URI
            val finalImageUri = if (item.imageUri.startsWith("content://")) {
                ImageUtils.copyImageToInternalStorage(context, Uri.parse(item.imageUri))
                    ?: return false // Return false if image copy failed
            } else {
                item.imageUri
            }

            val itemWithInternalUri = item.copy(imageUri = finalImageUri)
            clothingItemDao.insertClothingItem(itemWithInternalUri.toEntity())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateClothingItem(oldItem: ClothingItem, newItem: ClothingItem): Boolean {
        return try {
            // If image URI changed, copy new image and delete old one
            val finalImageUri = if (newItem.imageUri != oldItem.imageUri &&
                newItem.imageUri.startsWith("content://")) {
                val newInternalUri = ImageUtils.copyImageToInternalStorage(context, Uri.parse(newItem.imageUri))
                    ?: return false

                // Delete old image if it exists in internal storage
                if (oldItem.imageUri.startsWith("/")) {
                    ImageUtils.deleteImageFromInternalStorage(oldItem.imageUri)
                }

                newInternalUri
            } else {
                newItem.imageUri
            }

            val itemWithInternalUri = newItem.copy(imageUri = finalImageUri)
            clothingItemDao.updateClothingItem(itemWithInternalUri.toEntity())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removeClothingItem(item: ClothingItem): Boolean {
        return try {
            clothingItemDao.deleteClothingItem(item.toEntity())

            // Delete associated image file
            if (item.imageUri.startsWith("/")) {
                ImageUtils.deleteImageFromInternalStorage(item.imageUri)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
        // Handle model image URI if present
        val finalModelImageUri = outfit.modelImageUri?.let { uri ->
            if (uri.startsWith("content://")) {
                ImageUtils.copyImageToInternalStorage(context, Uri.parse(uri))
                    ?: uri // Keep original if copy fails
            } else {
                uri
            }
        }

        val outfitWithInternalUri = outfit.copy(modelImageUri = finalModelImageUri)

        outfitDao.insertOutfit(outfitWithInternalUri.toEntity())
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
        // Handle model image URI if present
        val finalModelImageUri = outfit.modelImageUri?.let { uri ->
            if (uri.startsWith("content://")) {
                ImageUtils.copyImageToInternalStorage(context, Uri.parse(uri))
                    ?: uri // Keep original if copy fails
            } else {
                uri
            }
        }

        val outfitWithInternalUri = outfit.copy(modelImageUri = finalModelImageUri)

        outfitDao.updateOutfit(outfitWithInternalUri.toEntity())
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

        // Delete model image if it exists in internal storage
        outfit.modelImageUri?.let { uri ->
            if (uri.startsWith("/")) {
                ImageUtils.deleteImageFromInternalStorage(uri)
            }
        }
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