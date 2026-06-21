package com.valtio.app.ui.settings

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valtio.app.R
import com.valtio.app.data.LanguagePreferences
import com.valtio.app.ui.components.PremiumHeader
import com.valtio.app.ui.theme.Primary
import com.valtio.app.util.LocaleManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    context: Context,
    onNavigateToExport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val languageFlow = remember { LanguagePreferences.languageFlow(context) }
    val current by languageFlow.collectAsState(initial = "auto")
    val scope = rememberCoroutineScope()
    var isLanguageExpanded by remember { mutableStateOf(false) }

    val options = listOf(
        Pair(stringResource(R.string.settings_language_auto), "auto"),
        Pair(stringResource(R.string.settings_language_pt), "pt"),
        Pair(stringResource(R.string.settings_language_en), "en"),
        Pair(stringResource(R.string.settings_language_es), "es"),
        Pair(stringResource(R.string.settings_language_fr), "fr"),
        Pair(stringResource(R.string.settings_language_de), "de"),
        Pair(stringResource(R.string.settings_language_it), "it"),
        Pair(stringResource(R.string.settings_language_nl), "nl")
    )

    Scaffold(
        topBar = {
            PremiumHeader(title = stringResource(R.string.nav_settings))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Export Item
                SettingsListItem(
                    icon = Icons.Filled.FileDownload,
                    title = stringResource(R.string.settings_export),
                    onClick = onNavigateToExport,
                    showChevron = true
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Language Expandable Item
                SettingsListItem(
                    icon = Icons.Filled.Language,
                    title = stringResource(R.string.settings_language),
                    onClick = { isLanguageExpanded = !isLanguageExpanded },
                    showChevron = false,
                    trailingIcon = if (isLanguageExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore
                )

                AnimatedVisibility(
                    visible = isLanguageExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        options.forEach { (label, tag) ->
                            LanguageItem(
                                label = label,
                                isSelected = current == tag,
                                onClick = {
                                    scope.launch {
                                        LanguagePreferences.saveLanguage(context, tag)
                                        LocaleManager.applyLocale(context, tag)
                                    }
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // About Item
                SettingsListItem(
                    icon = Icons.Filled.Info,
                    title = stringResource(R.string.settings_about),
                    onClick = onNavigateToAbout,
                    showChevron = true
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsListItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showChevron: Boolean = false,
    trailingIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (showChevron) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        } else if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LanguageItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(40.dp)) 
        
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
