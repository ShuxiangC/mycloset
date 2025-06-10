package com.example.mycloset.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits")
    fun getAllOutfits(): Flow<List<OutfitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("SELECT * FROM outfit_items WHERE outfitId = :outfitId")
    suspend fun getOutfitItems(outfitId: String): List<OutfitItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitItems(items: List<OutfitItemEntity>)

    @Query("DELETE FROM outfit_items WHERE outfitId = :outfitId")
    suspend fun deleteOutfitItems(outfitId: String)

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun getOutfitById(id: String): OutfitEntity?
}