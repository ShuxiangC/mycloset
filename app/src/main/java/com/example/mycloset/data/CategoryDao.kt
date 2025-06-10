package com.example.mycloset.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("UPDATE clothing_items SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateItemsCategory(oldCategory: String, newCategory: String)
}

// Data classes for queries
data class CategoryCount(
    val category: String,
    val count: Int
)