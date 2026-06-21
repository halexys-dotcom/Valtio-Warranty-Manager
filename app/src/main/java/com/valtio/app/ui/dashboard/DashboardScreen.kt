package com.valtio.app.ui.dashboard

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valtio.app.R
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.domain.model.Produto
import com.valtio.app.ui.components.EmptyState
import com.valtio.app.ui.components.GarantiaBadge
import com.valtio.app.ui.components.PremiumHeader
import com.valtio.app.ui.components.ProdutoCard
import com.valtio.app.ui.theme.Primary
import com.valtio.app.ui.theme.WarrantyActive
import com.valtio.app.ui.theme.WarrantyExpired
import com.valtio.app.ui.theme.WarrantyExpiring

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onProdutoClick: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PremiumHeader(title = stringResource(R.string.nav_home))
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.dashboard_summary),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DashboardCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.dashboard_active_warranties),
                                value = "${state.stats.ativas}",
                                icon = Icons.Filled.CheckCircle,
                                color = WarrantyActive
                            )
                            DashboardCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.dashboard_expiring_30d),
                                value = "${state.stats.aExpirar}",
                                icon = Icons.Filled.Warning,
                                color = WarrantyExpiring
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DashboardCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.dashboard_expired),
                                value = "${state.stats.expiradas}",
                                icon = Icons.Filled.Error,
                                color = WarrantyExpired
                            )
                            DashboardCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.dashboard_protected_value),
                                value = if (state.valorTotal > 0) String.format("%.0f €", state.valorTotal) else "0 €",
                                icon = Icons.Filled.Inventory2,
                                color = Primary
                            )
                        }
                    }

                    if (state.produtosExpirarBrevemente.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.dashboard_expiring_soon),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(state.produtosExpirarBrevemente) { produto ->
                            ProdutoExpirarCard(
                                produto = produto,
                                onClick = { onProdutoClick(produto.id) }
                            )
                        }
                    }

                    if (state.ultimosProdutos.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.dashboard_recent_products),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(state.ultimosProdutos) { produto ->
                            ProdutoCard(
                                produto = produto,
                                onClick = { onProdutoClick(produto.id) }
                            )
                        }
                    }

                    if (state.stats.total == 0) {
                        item {
                            Spacer(modifier = Modifier.height(48.dp))
                            EmptyState(
                                icon = Icons.Filled.Inbox,
                                title = stringResource(R.string.dashboard_empty_title),
                                subtitle = stringResource(R.string.dashboard_empty_subtitle)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ProdutoExpirarCard(
    produto: Produto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = WarrantyExpiring.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (produto.marca.isNotBlank()) {
                    Text(
                        text = produto.marca,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            GarantiaBadge(
                estado = EstadoGarantia.A_EXPIRAR,
                diasRestantes = produto.diasRestantes
            )
        }
    }
}
