package com.example.whatnow

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class FavoritesDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_FAVORITES = "favorites"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_URL = "url"
        private const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_FAVORITES (
                $COLUMN_URL TEXT PRIMARY KEY,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_IMAGE_URL TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }

    fun addFavorite(article: Article): Boolean {
        try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_URL, article.url)
                put(COLUMN_TITLE, article.title)
                put(COLUMN_IMAGE_URL, article.urlToImage)
            }
            
            val result = db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            db.close()
            return result != -1L
        } catch (e: Exception) {
            Log.e("FavoritesDatabase", "Error adding favorite: ${e.message}")
            return false
        }
    }

    fun removeFavorite(url: String): Boolean {
        try {
            val db = this.writableDatabase
            val result = db.delete(TABLE_FAVORITES, "$COLUMN_URL = ?", arrayOf(url))
            db.close()
            return result > 0
        } catch (e: Exception) {
            Log.e("FavoritesDatabase", "Error removing favorite: ${e.message}")
            return false
        }
    }

    fun isFavorite(url: String): Boolean {
        try {
            val db = this.readableDatabase
            val cursor = db.query(
                TABLE_FAVORITES,
                arrayOf(COLUMN_URL),
                "$COLUMN_URL = ?",
                arrayOf(url),
                null,
                null,
                null
            )
            val isFavorite = cursor.count > 0
            cursor.close()
            db.close()
            return isFavorite
        } catch (e: Exception) {
            Log.e("FavoritesDatabase", "Error checking favorite status: ${e.message}")
            return false
        }
    }

    fun getAllFavorites(): List<Article> {
        val favorites = mutableListOf<Article>()
        try {
            val db = this.readableDatabase
            val cursor = db.query(
                TABLE_FAVORITES,
                null,
                null,
                null,
                null,
                null,
                null
            )

            with(cursor) {
                while (moveToNext()) {
                    val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                    val url = getString(getColumnIndexOrThrow(COLUMN_URL))
                    val imageUrl = getString(getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                    favorites.add(Article(title, url, imageUrl, true))
                }
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            Log.e("FavoritesDatabase", "Error getting all favorites: ${e.message}")
        }
        return favorites
    }
}