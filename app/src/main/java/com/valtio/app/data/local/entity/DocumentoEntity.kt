package com.valtio.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "documentos",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["id"],
            childColumns = ["produto_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["produto_id"])]
)
data class DocumentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "produto_id")
    val produtoId: Long,

    @ColumnInfo(name = "tipo")
    val tipo: String, // "FOTO", "PDF", "OUTRO"

    @ColumnInfo(name = "caminho_ficheiro")
    val caminhoFicheiro: String,

    @ColumnInfo(name = "nome_original")
    val nomeOriginal: String,

    @ColumnInfo(name = "data_fatura")
    val dataFatura: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().toEpochMilli()
)