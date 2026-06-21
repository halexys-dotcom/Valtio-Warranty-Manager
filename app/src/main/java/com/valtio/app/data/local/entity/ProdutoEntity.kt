package com.valtio.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "marca")
    val marca: String = "",

    @ColumnInfo(name = "modelo")
    val modelo: String = "",

    @ColumnInfo(name = "categoria")
    val categoria: String = "",

    @ColumnInfo(name = "loja")
    val loja: String = "",

    @ColumnInfo(name = "preco")
    val preco: Double = 0.0,

    @ColumnInfo(name = "data_compra")
    val dataCompra: LocalDate,

    @ColumnInfo(name = "garantia_meses")
    val garantiaMeses: Int = 0,

    @ColumnInfo(name = "observacoes")
    val observacoes: String = "",

    @ColumnInfo(name = "foto_path")
    val fotoPath: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().toEpochMilli(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = Instant.now().toEpochMilli()
)