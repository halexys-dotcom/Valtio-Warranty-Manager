package com.valtio.app.ui.produto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valtio.app.R
import com.valtio.app.ui.components.EmptyState
import com.valtio.app.ui.components.PremiumHeader
import com.valtio.app.ui.components.ProdutoCard
import com.valtio.app.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoListScreen(
    onProdutoClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: ProdutoViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PremiumHeader(title = stringResource(R.string.products_title))
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            state.produtos.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    EmptyState(
                        icon = Icons.Filled.Inbox,
                        title = stringResource(R.string.products_empty_title),
                        subtitle = stringResource(R.string.products_empty_subtitle)
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(padding)
                ) {
                    items(state.produtos, key = { it.id }) { produto ->
                        ProdutoCard(
                            produto = produto,
                            onClick = { onProdutoClick(produto.id) },
                            onEditClick = { onEditClick(produto.id) }
                        )
                    }
                }
            }
        }
    }
}
