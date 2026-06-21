package com.valtio.app.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Produto(
    val id: Long = 0,
    val nome: String,
    val marca: String = "",
    val modelo: String = "",
    val categoria: String = "",
    val loja: String = "",
    val preco: Double = 0.0,
    val dataCompra: LocalDate,
    val garantiaMeses: Int = 0,
    val observacoes: String = "",
    val fotoPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val dataFimGarantia: LocalDate?
        get() = if (garantiaMeses > 0) dataCompra.plusMonths(garantiaMeses.toLong()) else null

    val diasRestantes: Long?
        get() = dataFimGarantia?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }

    val estadoGarantia: EstadoGarantia
        get() {
            val dias = diasRestantes ?: return EstadoGarantia.SEM_GARANTIA
            return when {
                dias < 0 -> EstadoGarantia.EXPIRADA
                dias <= 30 -> EstadoGarantia.A_EXPIRAR
                else -> EstadoGarantia.ATIVA
            }
        }
}

enum class EstadoGarantia(val label: String) {
    ATIVA("Ativa"),
    A_EXPIRAR("A Expirar"),
    EXPIRADA("Expirada"),
    SEM_GARANTIA("Sem Garantia")
}