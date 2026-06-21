package com.valtio.app.ui.pesquisa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valtio.app.data.repository.ProdutoRepository
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.domain.model.Produto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PesquisaUiState(
    val query: String = "",
    val filtroEstado: EstadoGarantia? = null,
    val resultados: List<Produto> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class PesquisaViewModel @Inject constructor(
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PesquisaUiState())
    val state: StateFlow<PesquisaUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _state.value = _state.value.copy(query = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            performSearch()
        }
    }

    fun onFiltroChanged(estado: EstadoGarantia?) {
        _state.value = _state.value.copy(filtroEstado = estado)
        performSearch()
    }

    private fun performSearch() {
        val query = _state.value.query.trim()
        val filtro = _state.value.filtroEstado

        viewModelScope.launch {
            _state.value = _state.value.copy(isSearching = true)

            if (query.isEmpty()) {
                produtoRepository.getAll().collect { produtos ->
                    val filtrados = if (filtro != null) {
                        produtos.filter { it.estadoGarantia == filtro }
                    } else {
                        produtos
                    }
                    _state.value = _state.value.copy(
                        resultados = filtrados,
                        isSearching = false
                    )
                }
            } else {
                produtoRepository.search(query).collect { produtos ->
                    val filtrados = if (filtro != null) {
                        produtos.filter { it.estadoGarantia == filtro }
                    } else {
                        produtos
                    }
                    _state.value = _state.value.copy(
                        resultados = filtrados,
                        isSearching = false
                    )
                }
            }
        }
    }
}