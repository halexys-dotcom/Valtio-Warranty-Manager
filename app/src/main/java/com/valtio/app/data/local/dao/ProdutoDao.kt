package com.valtio.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.valtio.app.data.local.entity.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(produto: ProdutoEntity): Long

    @Update
    suspend fun update(produto: ProdutoEntity)

    @Delete
    suspend fun delete(produto: ProdutoEntity)

    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun getById(id: Long): ProdutoEntity?

    @Query("SELECT * FROM produtos ORDER BY created_at DESC")
    fun getAll(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos ORDER BY created_at DESC")
    suspend fun getAllList(): List<ProdutoEntity>

    @Query("""
        SELECT * FROM produtos 
        WHERE nome LIKE '%' || :query || '%' 
           OR marca LIKE '%' || :query || '%' 
           OR loja LIKE '%' || :query || '%' 
           OR categoria LIKE '%' || :query || '%'
        ORDER BY created_at DESC
    """)
    fun search(query: String): Flow<List<ProdutoEntity>>

    @Query("SELECT COUNT(*) FROM produtos")
    suspend fun getCount(): Int
}