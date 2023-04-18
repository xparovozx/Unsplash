package com.example.unsplash.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unsplash.db.RemoteKeysDao
import com.example.unsplash.models.Photo
import com.example.unsplash.db.FeedRemoteKeys
import com.example.unsplash.db.PhotoDao

@Database(entities = [
    Photo::class,
    FeedRemoteKeys::class], version = 1)

abstract class UnsplashDatabase : RoomDatabase() {
    abstract fun unsplashImageDao(): PhotoDao
    abstract fun unsplashRemoteKeysDao(): RemoteKeysDao

    companion object Constants {
        const val DB_NAME = "UnsplashDatabase"
        const val UNSPLASH_TABLE = "unsplash_image_table"
        const val UNSPLASH_REMOTE_TABLE = "unsplash_remote_keys_table"
    }

}