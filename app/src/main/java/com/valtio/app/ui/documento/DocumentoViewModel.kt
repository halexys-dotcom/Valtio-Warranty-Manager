package com.valtio.app.ui.documento

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valtio.app.data.repository.DocumentoRepository
import com.valtio.app.domain.model.Documento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentoListUiState(
    val documentos: List<Documento> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DocumentoViewModel @Inject constructor(
    private val documentoRepository: DocumentoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DocumentoListUiState())
    val state: StateFlow<DocumentoListUiState> = _state.asStateFlow()

    fun loadDocumentos(produtoId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            documentoRepository.getByProdutoId(produtoId).collect { documentos ->
                _state.value = DocumentoListUiState(
                    documentos = documentos,
                    isLoading = false
                )
            }
        }
    }

    fun adicionarDocumento(produtoId: Long, uri: Uri, tipo: String, dataFatura: Long? = null) {
        viewModelScope.launch {
            documentoRepository.saveDocument(produtoId, uri, tipo, dataFatura)
        }
    }

    fun eliminarDocumento(documento: Documento) {
        viewModelScope.launch {
            documentoRepository.delete(documento)
        }
    }
}