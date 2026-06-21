package com.valtio.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valtio.app.data.repository.DashboardStats
import com.valtio.app.data.repository.ProdutoRepository
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.domain.model.Produto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val stats: DashboardStats = DashboardStats(0, 0, 0, 0),
    val valorTotal: Double = 0.0,
    val produtosExpirarBrevemente: List<Produto> = emptyList(),
    val ultimosProdutos: List<Produto> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val stats = produtoRepository.getDashboardStats()
            val valorTotal = produtoRepository.getValorTotal()
            val expirarBrevemente = produtoRepository.getProdutosComGarantiaAExpirarEm(30)
                .filter { it.estadoGarantia == EstadoGarantia.A_EXPIRAR }
                .sortedBy { it.diasRestantes }
            val ultimos = produtoRepository.getUltimosProdutos(5)
            _state.value = DashboardUiState(
                stats = stats,
                valorTotal = valorTotal,
                produtosExpirarBrevemente = expirarBrevemente,
                ultimosProdutos = ultimos,
                isLoading = false
            )
        }
    }
}
