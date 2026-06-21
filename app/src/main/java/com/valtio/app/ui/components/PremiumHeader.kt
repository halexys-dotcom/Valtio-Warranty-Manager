package com.valtio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.valtio.app.R
import com.valtio.app.ui.theme.Primary

@Composable
fun PremiumHeader(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Primary)
    ) {
        // Logo with overflow effect, centered vertically with the title
        Icon(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp) // Pushed more to the left for better symmetry
                .size(60.dp), // Slightly smaller logo for more elegance
            tint = Color.Unspecified
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = if (onNavigateBack == null) 16.dp else 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.product_detail_back),
                        tint = Color.White
                    )
                }
            }

            Text(
                text = title,
                color = Color.White,
                fontSize = 28.sp, // Slightly smaller title for better alignment
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            if (actions != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    actions()
                }
            }
        }
    }
}

@Preview
@Composable
fun PremiumHeaderPreview() {
    PremiumHeader(title = "Início")
}
