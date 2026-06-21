package com.valtio.app.ui.pesquisa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valtio.app.R
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.ui.components.EmptyState
import com.valtio.app.ui.components.PremiumHeader
import com.valtio.app.ui.components.ProdutoCard
import com.valtio.app.ui.theme.Primary
import com.valtio.app.ui.theme.WarrantyActive
import com.valtio.app.ui.theme.WarrantyExpired
import com.valtio.app.ui.theme.WarrantyExpiring

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesquisaScreen(
    onProdutoClick: (Long) -> Unit,
    viewModel: PesquisaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PremiumHeader(title = stringResource(R.string.search_title))
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.search_clear), modifier = Modifier.padding(0.dp))
                    }
                },
                singleLine = true
            )

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = state.filtroEstado == null, onClick = { viewModel.onFiltroChanged(null) }, label = { Text(stringResource(R.string.search_filter_all)) })
                FilterChip(selected = state.filtroEstado == EstadoGarantia.ATIVA, onClick = { viewModel.onFiltroChanged(EstadoGarantia.ATIVA) }, label = { Text(stringResource(R.string.search_filter_active)) }, leadingIcon = { Icon(Icons.Filled.CheckCircle, contentDescription = null) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarrantyActive.copy(alpha = 0.2f), selectedLabelColor = WarrantyActive))
                FilterChip(selected = state.filtroEstado == EstadoGarantia.A_EXPIRAR, onClick = { viewModel.onFiltroChanged(EstadoGarantia.A_EXPIRAR) }, label = { Text(stringResource(R.string.search_filter_expiring)) }, leadingIcon = { Icon(Icons.Filled.Warning, contentDescription = null) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarrantyExpiring.copy(alpha = 0.2f), selectedLabelColor = WarrantyExpiring))
                FilterChip(selected = state.filtroEstado == EstadoGarantia.EXPIRADA, onClick = { viewModel.onFiltroChanged(EstadoGarantia.EXPIRADA) }, label = { Text(stringResource(R.string.search_filter_expired)) }, leadingIcon = { Icon(Icons.Filled.Error, contentDescription = null) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarrantyExpired.copy(alpha = 0.2f), selectedLabelColor = WarrantyExpired))
            }

            if (state.isSearching) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            } else if (state.resultados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    EmptyState(
                        icon = Icons.Filled.Inbox,
                        title = if (state.query.isNotEmpty()) stringResource(R.string.search_no_results) else stringResource(R.string.search_no_products),
                        subtitle = if (state.query.isNotEmpty()) stringResource(R.string.search_try_other_terms) else ""
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.resultados, key = { it.id }) { produto -> ProdutoCard(produto = produto, onClick = { onProdutoClick(produto.id) }) }
                }
            }
        }
    }
}
