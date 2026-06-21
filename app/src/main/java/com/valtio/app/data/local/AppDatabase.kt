package com.valtio.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.valtio.app.data.local.converter.Converters
import com.valtio.app.data.local.dao.DocumentoDao
import com.valtio.app.data.local.dao.ProdutoDao
import com.valtio.app.data.local.entity.DocumentoEntity
import com.valtio.app.data.local.entity.ProdutoEntity

@Database(
    entities = [
        ProdutoEntity::class,
        DocumentoEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun produtoDao(): ProdutoDao
    abstract fun documentoDao(): DocumentoDao
}