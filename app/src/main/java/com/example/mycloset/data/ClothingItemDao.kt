package com.example.mycloset.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Query("SELECT * FROM clothing_items")
    fun getAllClothingItems(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE category = :category")
    fun getItemsByCategory(category: String): Flow<List<ClothingItemEntity>>

    @Query("SELECT category, COUNT(*) as count FROM clothing_items GROUP BY category")
    fun getCategoryCounts(): Flow<List<CategoryCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingItem(item: ClothingItemEntity)

    @Update
    suspend fun updateClothingItem(item: ClothingItemEntity)

    @Delete
    suspend fun deleteClothingItem(item: ClothingItemEntity)

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getClothingItemById(id: String): ClothingItemEntity?
}