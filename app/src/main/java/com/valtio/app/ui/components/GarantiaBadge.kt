package com.valtio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valtio.app.R
import com.valtio.app.domain.model.EstadoGarantia
import com.valtio.app.ui.theme.WarrantyActive
import com.valtio.app.ui.theme.WarrantyExpired
import com.valtio.app.ui.theme.WarrantyExpiring
import com.valtio.app.ui.theme.WarrantyNoWarranty

@Composable
fun GarantiaBadge(estado: EstadoGarantia, diasRestantes: Long? = null) {
    val (backgroundColor, textColor, texto) = when (estado) {
        EstadoGarantia.ATIVA -> Triple(
            WarrantyActive.copy(alpha = 0.15f),
            WarrantyActive,
            if (diasRestantes != null) stringResource(R.string.badge_active, diasRestantes.toInt()) else stringResource(R.string.badge_active_short)
        )
        EstadoGarantia.A_EXPIRAR -> Triple(
            WarrantyExpiring.copy(alpha = 0.15f),
            WarrantyExpiring,
            if (diasRestantes != null) stringResource(R.string.badge_expiring, diasRestantes.toInt()) else stringResource(R.string.badge_expiring_short)
        )
        EstadoGarantia.EXPIRADA -> Triple(
            WarrantyExpired.copy(alpha = 0.15f),
            WarrantyExpired,
            stringResource(R.string.badge_expired_short)
        )
        EstadoGarantia.SEM_GARANTIA -> Triple(
            WarrantyNoWarranty.copy(alpha = 0.15f),
            WarrantyNoWarranty,
            stringResource(R.string.badge_none_short)
        )
    }

    Text(
        text = texto,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}