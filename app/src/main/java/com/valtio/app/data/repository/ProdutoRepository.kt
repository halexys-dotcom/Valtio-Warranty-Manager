package com.valtio.app.data.repository

import com.valtio.app.data.local.dao.ProdutoDao
import com.valtio.app.data.local.entity.ProdutoEntity
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.domain.model.Produto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdutoRepository @Inject constructor(
    private val produtoDao: ProdutoDao
) {
    fun getAll(): Flow<List<Produto>> = produtoDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getAllList(): List<Produto> = produtoDao.getAllList().map { it.toDomain() }

    suspend fun getById(id: Long): Produto? = produtoDao.getById(id)?.toDomain()

    suspend fun insert(produto: Produto): Long {
        return produtoDao.insert(produto.toEntity())
    }

    suspend fun update(produto: Produto) {
        produtoDao.update(produto.toEntity())
    }

    suspend fun delete(produto: Produto) {
        produtoDao.delete(produto.toEntity())
    }

    fun search(query: String): Flow<List<Produto>> = produtoDao.search(query).map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getCount(): Int = produtoDao.getCount()

    suspend fun getCountByStatus(estado: EstadoGarantia): Int {
        val all = getAllList()
        return all.count { it.estadoGarantia == estado }
    }

    suspend fun getDashboardStats(): DashboardStats {
        val all = getAllList()
        val ativas = all.count { it.estadoGarantia == EstadoGarantia.ATIVA }
        val aExpirar = all.count { it.estadoGarantia == EstadoGarantia.A_EXPIRAR }
        val expiradas = all.count { it.estadoGarantia == EstadoGarantia.EXPIRADA }
        return DashboardStats(
            total = all.size,
            ativas = ativas,
            aExpirar = aExpirar,
            expiradas = expiradas
        )
    }

    suspend fun getProdutosComGarantiaAExpirarEm(dias: Int): List<Produto> {
        val all = getAllList()
        val hoje = LocalDate.now()
        return all.filter { produto ->
            produto.dataFimGarantia != null &&
            ChronoUnit.DAYS.between(hoje, produto.dataFimGarantia) in 0..dias
        }
    }

    suspend fun getValorTotal(): Double {
        val all = getAllList()
        return all.sumOf { it.preco }
    }

    suspend fun getUltimosProdutos(limit: Int = 5): List<Produto> {
        return produtoDao.getAllList()
            .sortedByDescending { it.createdAt }
            .take(limit)
            .map { it.toDomain() }
    }

    private fun ProdutoEntity.toDomain(): Produto = Produto(
        id = id,
        nome = nome,
        marca = marca,
        modelo = modelo,
        categoria = categoria,
        loja = loja,
        preco = preco,
        dataCompra = dataCompra,
        garantiaMeses = garantiaMeses,
        observacoes = observacoes,
        fotoPath = fotoPath,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Produto.toEntity(): ProdutoEntity = ProdutoEntity(
        id = id,
        nome = nome,
        marca = marca,
        modelo = modelo,
        categoria = categoria,
        loja = loja,
        preco = preco,
        dataCompra = dataCompra,
        garantiaMeses = garantiaMeses,
        observacoes = observacoes,
        fotoPath = fotoPath,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}

data class DashboardStats(
    val total: Int,
    val ativas: Int,
    val aExpirar: Int,
    val expiradas: Int
)