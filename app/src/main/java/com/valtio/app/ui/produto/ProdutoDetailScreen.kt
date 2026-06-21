package com.valtio.app.ui.produto

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.valtio.app.R
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.ui.components.GarantiaBadge
import com.valtio.app.ui.components.PremiumHeader
import com.valtio.app.ui.documento.DocumentoViewModel
import com.valtio.app.ui.theme.Error
import com.valtio.app.ui.theme.Primary
import java.io.File
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoDetailScreen(
    produtoId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    produtoViewModel: ProdutoViewModel = hiltViewModel(),
    documentoViewModel: DocumentoViewModel = hiltViewModel()
) {
    val produtoState by produtoViewModel.detailState.collectAsStateWithLifecycle()
    val documentoState by documentoViewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { documentoViewModel.adicionarDocumento(produtoId, it, "FOTO") }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { documentoViewModel.adicionarDocumento(produtoId, it, "PDF") }
    }

    LaunchedEffect(produtoId) {
        produtoViewModel.loadProduto(produtoId)
        documentoViewModel.loadDocumentos(produtoId)
    }

    LaunchedEffect(produtoState.deletado) {
        if (produtoState.deletado) onNavigateBack()
    }

    Scaffold(
        topBar = {
            PremiumHeader(
                title = stringResource(R.string.product_detail_title_short),
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { onEditClick(produtoId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.product_detail_edit), tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.product_detail_delete), tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (produtoState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            val produto = produtoState.produto
            if (produto == null) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.product_detail_not_found))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(padding)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = produto.nome, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                if (produto.marca.isNotBlank()) {
                                    Text(text = produto.marca, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            GarantiaBadge(estado = produto.estadoGarantia, diasRestantes = produto.diasRestantes)
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow(stringResource(R.string.product_detail_model), produto.modelo)
                                DetailRow(stringResource(R.string.product_detail_category), produto.categoria)
                                DetailRow(stringResource(R.string.product_detail_store), produto.loja)
                                DetailRow(stringResource(R.string.product_detail_price), if (produto.preco > 0) String.format("%.2f €", produto.preco) else "-")
                                DetailRow(stringResource(R.string.product_detail_purchase_date), produto.dataCompra.format(dateFormatter))
                                DetailRow(stringResource(R.string.product_detail_warranty), if (produto.garantiaMeses > 0) "${produto.garantiaMeses} meses" else stringResource(R.string.product_form_no_warranty))
                                
                                val dataFim = produto.dataFimGarantia
                                if (dataFim != null) {
                                    DetailRow(stringResource(R.string.product_detail_warranty_end), dataFim.format(dateFormatter))
                                }
                                
                                if (produto.diasRestantes != null && produto.estadoGarantia == EstadoGarantia.ATIVA) {
                                    DetailRow(stringResource(R.string.product_detail_days_remaining), "${produto.diasRestantes} dias")
                                }
                                
                                if (produto.observacoes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = stringResource(R.string.product_detail_notes), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text(text = produto.observacoes, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(R.string.product_detail_documents, documentoState.documentos.size.toString()), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row {
                                IconButton(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                                    Icon(Icons.Filled.CameraAlt, contentDescription = "Foto", tint = Primary)
                                }
                                IconButton(onClick = { pdfPicker.launch("application/pdf") }) {
                                    Icon(Icons.Filled.PictureAsPdf, contentDescription = "PDF", tint = Primary)
                                }
                            }
                        }
                    }

                    if (documentoState.documentos.isEmpty()) {
                        item { Text(text = stringResource(R.string.product_detail_no_documents), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }
                    } else {
                        items(documentoState.documentos, key = { it.id }) { doc ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (doc.isImagem) {
                                        AsyncImage(model = File(doc.caminhoFicheiro), contentDescription = doc.nomeOriginal, modifier = Modifier.size(56.dp), contentScale = ContentScale.Crop)
                                    } else {
                                        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = Error, modifier = Modifier.size(32.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = doc.nomeOriginal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Text(text = doc.tipo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                    IconButton(onClick = { documentoViewModel.eliminarDocumento(doc) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.product_detail_delete), tint = Error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.product_detail_delete)) },
            text = { Text(stringResource(R.string.product_detail_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; produtoState.produto?.let { produtoViewModel.eliminarProduto(it) } }, colors = ButtonDefaults.textButtonColors(contentColor = Error)) {
                    Text(stringResource(R.string.product_detail_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.product_detail_cancel)) }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    if (value.isNotBlank() && value != "-") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.6f),
                textAlign = TextAlign.End
            )
        }
    }
}
