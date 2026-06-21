package com.valtio.app.domain.model

data class Documento(
    val id: Long = 0,
    val produtoId: Long,
    val tipo: String,
    val caminhoFicheiro: String,
    val nomeOriginal: String,
    val dataFatura: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isImagem: Boolean get() = tipo == "FOTO"
    val isPdf: Boolean get() = tipo == "PDF"
}