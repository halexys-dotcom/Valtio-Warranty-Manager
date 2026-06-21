package com.valtio.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.valtio.app.data.local.AppDatabase
import com.valtio.app.data.local.dao.DocumentoDao
import com.valtio.app.data.local.dao.ProdutoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val migration_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE documentos ADD COLUMN data_fatura INTEGER")
            }
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "valtio_database"
        )
            .addMigrations(migration_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProdutoDao(database: AppDatabase): ProdutoDao {
        return database.produtoDao()
    }

    @Provides
    fun provideDocumentoDao(database: AppDatabase): DocumentoDao {
        return database.documentoDao()
    }
}