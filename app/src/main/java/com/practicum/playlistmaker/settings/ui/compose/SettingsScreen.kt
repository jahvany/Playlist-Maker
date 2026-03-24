package com.practicum.playlistmaker.settings.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.ui.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    isDarkThemeEnabled: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    onShareClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onAgreementClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {

        CommonTopBar(stringResource(R.string.settings))

        // Theme Switch
        SettingsItem(
            text = stringResource(R.string.darkTheme),
            isSwitch = true,
            isChecked = isDarkThemeEnabled,
            onCheckedChange = onThemeToggle,
            //modifier = Modifier.clickable(enabled = false) { } // Disable click on the whole item
        )

        // Share Button
        SettingsItem(
            text = stringResource(R.string.share),
            painter = painterResource(R.drawable.share),
            onClick = onShareClick
        )

        // Support Button
        SettingsItem(
            text = stringResource(R.string.support),
            painter = painterResource(R.drawable.support),
            onClick = onSupportClick
        )

        // Agreement Button
        SettingsItem(
            text = stringResource(R.string.agreement),
            painter = painterResource(R.drawable.arrowforward),
            onClick = onAgreementClick
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    painter: androidx.compose.ui.graphics.painter.Painter? = null,
    isSwitch: Boolean = false,
    isChecked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        when {
            isSwitch && onCheckedChange != null -> {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )
            }
            painter != null -> {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}