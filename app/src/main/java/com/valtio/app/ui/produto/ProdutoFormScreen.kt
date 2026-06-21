package com.valtio.app.ui.produto

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.valtio.app.R
import com.valtio.app.domain.model.Produto
import com.valtio.app.ui.components.PremiumHeader
import com.valtio.app.ui.theme.Primary
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoFormScreen(
    produtoId: Long?,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProdutoViewModel = hiltViewModel()
) {
    val isEditing = produtoId != null
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    var nome by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var loja by remember { mutableStateOf("") }
    var preco by remember { mutableDoubleStateOf(0.0) }
    var precoText by remember { mutableStateOf("") }
    var garantiaMeses by remember { mutableIntStateOf(0) }
    var garantiaExpanded by remember { mutableStateOf(false) }
    val garantiaOpcoes = listOf(12, 24, 36, 48, 60)
    var fotoPath by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var observacoes by remember { mutableStateOf("") }
    var dataCompra by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            fotoUri = it
            fotoPath = copyPhotoToInternal(context, it)
        }
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    val noWarrantyLabel = stringResource(R.string.product_form_no_warranty)
    val customWarrantyLabel = stringResource(R.string.product_form_warranty_custom)
    val monthsLabelFormat = stringResource(R.string.product_form_warranty_months, 100) // template

    LaunchedEffect(produtoId) {
        if (isEditing && !isLoaded) { viewModel.loadProduto(produtoId!!); isLoaded = true }
    }

    LaunchedEffect(state.produto) {
        if (isEditing && state.produto != null && !isLoaded) {
            val p = state.produto!!
            nome = p.nome; marca = p.marca; modelo = p.modelo; categoria = p.categoria; loja = p.loja
            preco = p.preco; precoText = if (p.preco > 0) String.format("%.2f", p.preco) else ""
            garantiaMeses = p.garantiaMeses; observacoes = p.observacoes; fotoPath = p.fotoPath
            if (p.fotoPath.isNotBlank() && File(p.fotoPath).exists()) fotoUri = Uri.fromFile(File(p.fotoPath))
            dataCompra = p.dataCompra; isLoaded = true
        }
    }

    Scaffold(
        topBar = {
            PremiumHeader(
                title = stringResource(if (isEditing) R.string.product_form_edit_title_short else R.string.product_form_new_title_short),
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(padding)) {
            item {
                Text(text = stringResource(R.string.product_form_info), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (fotoPath.isNotBlank() && File(fotoPath).exists()) {
                        AsyncImage(model = File(fotoPath), contentDescription = "Foto do produto", modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    }
                    OutlinedButton(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (fotoPath.isBlank()) stringResource(R.string.product_form_add_photo) else stringResource(R.string.product_form_change_photo))
                    }
                }
            }

            item { OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text(stringResource(R.string.product_form_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text(stringResource(R.string.product_form_brand)) }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text(stringResource(R.string.product_form_model)) }, modifier = Modifier.weight(1f), singleLine = true)
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text(stringResource(R.string.product_form_category)) }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = loja, onValueChange = { loja = it }, label = { Text(stringResource(R.string.product_form_store)) }, modifier = Modifier.weight(1f), singleLine = true)
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = precoText, onValueChange = { precoText = it; preco = it.toDoubleOrNull() ?: 0.0 }, label = { Text(stringResource(R.string.product_form_price)) }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), prefix = { Text(stringResource(R.string.currency_eur_prefix)) })

                    ExposedDropdownMenuBox(expanded = garantiaExpanded, onExpandedChange = { garantiaExpanded = it }, modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = when {
                                garantiaMeses == 0 -> noWarrantyLabel
                                garantiaMeses == -1 -> customWarrantyLabel
                                else -> stringResource(R.string.product_form_warranty_months, garantiaMeses)
                            }, 
                            onValueChange = {}, 
                            readOnly = true, 
                            label = { Text(stringResource(R.string.product_form_warranty)) }, 
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = garantiaExpanded) }, 
                            modifier = Modifier.menuAnchor(), 
                            singleLine = true
                        )
                        ExposedDropdownMenu(expanded = garantiaExpanded, onDismissRequest = { garantiaExpanded = false }) {
                            DropdownMenuItem(text = { Text(noWarrantyLabel) }, onClick = { garantiaMeses = 0; garantiaExpanded = false })
                            garantiaOpcoes.forEach { m -> DropdownMenuItem(text = { Text(stringResource(R.string.product_form_warranty_months, m)) }, onClick = { garantiaMeses = m; garantiaExpanded = false }) }
                            DropdownMenuItem(text = { Text(customWarrantyLabel) }, onClick = { garantiaMeses = -1; garantiaExpanded = false })
                        }
                    }
                }
                if (garantiaMeses == -1) {
                    OutlinedTextField(value = "", onValueChange = { text -> garantiaMeses = text.toIntOrNull() ?: -1 }, label = { Text(stringResource(R.string.product_form_warranty_custom_hint)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }

            item {
                OutlinedTextField(value = dataCompra.format(dateFormatter), onValueChange = {}, label = { Text(stringResource(R.string.product_form_purchase_date)) }, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, readOnly = true, enabled = false, trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = stringResource(R.string.product_form_select_date)) })
            }

            item { OutlinedTextField(value = observacoes, onValueChange = { observacoes = it }, label = { Text(stringResource(R.string.product_form_notes)) }, modifier = Modifier.fillMaxWidth(), maxLines = 3) }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val mesesFinal = if (garantiaMeses == -1) 0 else garantiaMeses
                        viewModel.salvarProduto(Produto(id = if (isEditing) produtoId!! else 0L, nome = nome, marca = marca, modelo = modelo, categoria = categoria, loja = loja, preco = preco, dataCompra = dataCompra, garantiaMeses = mesesFinal, observacoes = observacoes, fotoPath = fotoPath, createdAt = if (isEditing && state.produto != null) state.produto!!.createdAt else System.currentTimeMillis(), updatedAt = System.currentTimeMillis()), isEditing)
                        onSaved()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), enabled = nome.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(text = stringResource(if (isEditing) R.string.product_form_save_edit else R.string.product_form_add_product), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataCompra.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { dataCompra = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showDatePicker = false }) { Text(stringResource(R.string.product_form_ok)) } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.product_form_cancel)) } }
        ) { DatePicker(state = datePickerState) }
    }
}

private fun copyPhotoToInternal(context: android.content.Context, uri: Uri): String {
    val dir = File(context.filesDir, "valtio_photos")
    if (!dir.exists()) dir.mkdirs()
    val fileName = "produto_${System.currentTimeMillis()}.jpg"
    val destFile = File(dir, fileName)
    context.contentResolver.openInputStream(uri)?.use { input -> FileOutputStream(destFile).use { output -> input.copyTo(output) } }
    return destFile.absolutePath
}
