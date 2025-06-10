package com.example.mycloset.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        ClothingItemEntity::class,
        OutfitEntity::class,
        OutfitItemEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitDao(): OutfitDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mycloset_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}