package com.valtio.app.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.net.Uri
import com.valtio.app.data.local.dao.DocumentoDao
import com.valtio.app.data.local.entity.DocumentoEntity
import com.valtio.app.domain.model.Documento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentoRepository @Inject constructor(
    private val documentoDao: DocumentoDao,
    @ApplicationContext private val context: Context
) {
    fun getAll(): Flow<List<Documento>> = documentoDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getByProdutoId(produtoId: Long): Flow<List<Documento>> =
        documentoDao.getByProdutoId(produtoId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun getByProdutoIdList(produtoId: Long): List<Documento> =
        documentoDao.getByProdutoIdList(produtoId).map { it.toDomain() }

    suspend fun getById(id: Long): Documento? = documentoDao.getById(id)?.toDomain()

    suspend fun insert(documento: Documento): Long {
        return documentoDao.insert(documento.toEntity())
    }

    suspend fun delete(documento: Documento) {
        // Remove physical file
        val file = File(documento.caminhoFicheiro)
        if (file.exists()) file.delete()
        documentoDao.delete(documento.toEntity())
    }

    suspend fun getCountByProduto(produtoId: Long): Int =
        documentoDao.getCountByProduto(produtoId)

    suspend fun saveDocument(produtoId: Long, uri: Uri, tipo: String, dataFatura: Long? = null): Documento {
        val nomeOriginal = getFileName(uri) ?: "documento_${Instant.now().toEpochMilli()}"
        val savedPath = copyToInternalStorage(uri, nomeOriginal)

        val documento = Documento(
            produtoId = produtoId,
            tipo = tipo,
            caminhoFicheiro = savedPath,
            nomeOriginal = nomeOriginal,
            dataFatura = dataFatura
        )
        val id = insert(documento)
        return documento.copy(id = id)
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun copyToInternalStorage(uri: Uri, fileName: String): String {
        val dir = File(context.filesDir, "valtio_documents")
        if (!dir.exists()) dir.mkdirs()

        val uniqueName = "${System.currentTimeMillis()}_$fileName"
        val destFile = File(dir, uniqueName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        return destFile.absolutePath
    }

    private fun DocumentoEntity.toDomain(): Documento = Documento(
        id = id,
        produtoId = produtoId,
        tipo = tipo,
        caminhoFicheiro = caminhoFicheiro,
        nomeOriginal = nomeOriginal,
        dataFatura = dataFatura,
        createdAt = createdAt
    )

    private fun Documento.toEntity(): DocumentoEntity = DocumentoEntity(
        id = id,
        produtoId = produtoId,
        tipo = tipo,
        caminhoFicheiro = caminhoFicheiro,
        nomeOriginal = nomeOriginal,
        dataFatura = dataFatura,
        createdAt = createdAt
    )
}