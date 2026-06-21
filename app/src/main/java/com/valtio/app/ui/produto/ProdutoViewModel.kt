package com.valtio.app.ui.produto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valtio.app.data.repository.ProdutoRepository
import com.valtio.app.domain.model.Produto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProdutoListUiState(
    val produtos: List<Produto> = emptyList(),
    val isLoading: Boolean = true
)

data class ProdutoDetailUiState(
    val produto: Produto? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val deletado: Boolean = false
)

@HiltViewModel
class ProdutoViewModel @Inject constructor(
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(ProdutoListUiState())
    val listState: StateFlow<ProdutoListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ProdutoDetailUiState())
    val detailState: StateFlow<ProdutoDetailUiState> = _detailState.asStateFlow()

    init {
        loadProdutos()
    }

    private fun loadProdutos() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true)
            produtoRepository.getAll().collect { produtos ->
                _listState.value = ProdutoListUiState(
                    produtos = produtos,
                    isLoading = false
                )
            }
        }
    }

    fun loadProduto(id: Long) {
        viewModelScope.launch {
            _detailState.value = ProdutoDetailUiState(isLoading = true)
            val produto = produtoRepository.getById(id)
            _detailState.value = ProdutoDetailUiState(
                produto = produto,
                isLoading = false
            )
        }
    }

    fun salvarProduto(produto: Produto, isEditing: Boolean) {
        viewModelScope.launch {
            if (isEditing) {
                produtoRepository.update(produto)
            } else {
                produtoRepository.insert(produto)
            }
        }
    }

    fun eliminarProduto(produto: Produto) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isDeleting = true)
            produtoRepository.delete(produto)
            _detailState.value = _detailState.value.copy(
                isDeleting = false,
                deletado = true
            )
        }
    }
}