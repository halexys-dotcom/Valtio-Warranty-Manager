package com.valtio.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valtio.app.data.local.entity.DocumentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(documento: DocumentoEntity): Long

    @Delete
    suspend fun delete(documento: DocumentoEntity)

    @Query("SELECT * FROM documentos WHERE id = :id")
    suspend fun getById(id: Long): DocumentoEntity?

    @Query("SELECT * FROM documentos WHERE produto_id = :produtoId ORDER BY created_at DESC")
    fun getByProdutoId(produtoId: Long): Flow<List<DocumentoEntity>>

    @Query("SELECT * FROM documentos WHERE produto_id = :produtoId ORDER BY created_at DESC")
    suspend fun getByProdutoIdList(produtoId: Long): List<DocumentoEntity>

    @Query("SELECT COUNT(*) FROM documentos WHERE produto_id = :produtoId")
    suspend fun getCountByProduto(produtoId: Long): Int

    @Query("SELECT * FROM documentos ORDER BY created_at DESC")
    fun getAll(): Flow<List<DocumentoEntity>>
}