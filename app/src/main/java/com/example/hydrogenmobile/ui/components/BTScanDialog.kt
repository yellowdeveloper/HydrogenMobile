package com.example.hydrogenmobile.ui.components

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.items
import com.example.hydrogenmobile.viewmodels.BTScanViewModel

@Composable
fun BTScanDialog(
    viewModel: BTScanViewModel,
    onDismissRequest: () -> Unit,
    onDeviceSelected: (ScanResult) -> Unit
) {
    val devices by viewModel.scanned_devices.collectAsState()

    DisposableEffect(Unit) {
        viewModel.ScanStart()

        onDispose {
            viewModel.ScanStop()
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "기기 검색 중...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (devices.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator() // 로딩 표시
                    }
                } else {
                    LazyColumn {
                        items(devices) { device ->
                            DeviceItem(device, onDeviceSelected)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(scanResult: ScanResult, onClick: (ScanResult) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(scanResult) }
            .padding(vertical = 8.dp)
    ) {
        Text(text = scanResult.device.name ?: "Unknown Device", fontWeight = FontWeight.Bold)
        Text(text = scanResult.device.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}


