package com.example.toolsbox

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.zip.ZipInputStream

class DatabaseHelper(private val context: Context) {
    private val dbName = "dict-cn.db" // 你的数据库文件名
    private val dbPath = context.getDatabasePath(dbName).path
    private val zipAssetName = "res/dict-cn.zip" // assets 中的压缩文件名

    fun initializeDatabase(): Boolean {
        // 检查数据库是否已存在
        if (checkDatabaseExists()) {
            return true
        }

        return try {
            // 从 assets 中解压数据库
            copyDatabaseFromZip()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun checkDatabaseExists(): Boolean {
        val dbFile = File(dbPath)
        return dbFile.exists()
    }

    @Throws(IOException::class)
    private fun copyDatabaseFromZip() {
        // 确保父目录存在
        File(dbPath).parentFile?.mkdirs()

        context.assets.open(zipAssetName).use { zipInputStream ->
            ZipInputStream(zipInputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name == dbName && !entry.isDirectory) {
                        FileOutputStream(dbPath).use { output ->
                            zis.copyTo(output)
                        }
                        break
                    }
                    entry = zis.nextEntry
                }
            }
        }
    }

    private fun addToResul(result: JSONArray, table: String, cursor: Cursor) {
        val item = JSONObject()
        item.put("table", table)
        for (i in 0 until cursor.columnCount) {
            item.put(cursor.getColumnName(i), cursor.getString(i))
        }
        result.put(item)
    }

    fun searchKeyword(
        keyword: String, includeWordDb: Boolean, includeIdiomDb: Boolean, includeXhyDb: Boolean
    ): String {
        if (!checkDatabaseExists()) {
            if (!initializeDatabase()) {
                return "[]"
            }
        }

        if (keyword.isEmpty()) {
            return "[]"
        }

        val results = JSONArray()
        var db: SQLiteDatabase? = null
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
            if (includeWordDb) {
                val queryWord = """
                    SELECT word, oldword, pinyin, radicals, strokes, explanation 
                    FROM word 
                    WHERE INSTR(?, word) > 0 
                    ORDER BY INSTR(?, word) ASC
                    """.trimIndent()
                db.rawQuery(queryWord, arrayOf(keyword, keyword)).use { cursor ->
                    while (cursor.moveToNext()) {
                        addToResul(results, "word", cursor)
                    }
                }
            }

            val like = Utils.addPercentSigns(keyword)

            if (includeIdiomDb) {
                val queryIdiom = """
                    SELECT word, pinyin, derivation, explanation, example 
                    FROM idiom 
                    WHERE word LIKE ?
                    """.trimIndent()
                db.rawQuery(queryIdiom, arrayOf(like)).use { cursor ->
                    while (cursor.moveToNext()) {
                        addToResul(results, "idiom", cursor)
                    }
                }
            }

            if (includeXhyDb) {
                val queryXhy = """
                    SELECT riddle, answer 
                    FROM xiehouyu 
                    WHERE (riddle LIKE ? OR answer LIKE ?)
                    """.trimIndent()
                db.rawQuery(queryXhy, arrayOf(like, like)).use { cursor ->
                    while (cursor.moveToNext()) {
                        addToResul(results, "xhy", cursor)
                    }
                }
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } finally {
            db?.close()
        }

        return results.toString()
    }
}