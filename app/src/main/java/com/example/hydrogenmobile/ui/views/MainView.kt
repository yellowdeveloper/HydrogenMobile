package com.example.hydrogenmobile.ui.views

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrogenmobile.ui.components.BTScanDialog
import com.example.hydrogenmobile.R
import com.example.hydrogenmobile.models.BTModel
import com.example.hydrogenmobile.models.BtUiEvent
import com.example.hydrogenmobile.ui.components.defaultCard
import com.example.hydrogenmobile.viewmodels.BTDataViewModel
import com.example.hydrogenmobile.viewmodels.BTScanViewModel
import com.example.hydrogenmobile.viewmodels.ViewModelFactory
import com.example.hydrogenmobile.utils.CircleDrawing
import kotlinx.coroutines.delay

@Composable
fun MainScreenForm(btModel: BTModel) {
    val focusManager = LocalFocusManager.current

    val viewModel: BTDataViewModel = viewModel(
        factory = ViewModelFactory(btModel)
    )
    val finalData by viewModel.finalData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(100)
        focusManager.clearFocus()
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .focusable()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {

            // Header Panel Area
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Black)
                .addFocusCleaner(focusManager),
            ){
                HeaderPanel(btModel)
            }

            // Main Panel Area
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color.Red)
                .addFocusCleaner(focusManager)
            ) {
                // Control Panels
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .padding(10.dp, 10.dp, 10.dp, 5.dp)
                    ) {
                        // Area1 (RawCard | LPF Card)
                        defaultCard(modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                            .padding(end = 5.dp),
                            filterName = "RAW",
                            data = finalData?.raw,
                            viewModel.rawBuffer
                        )
                        defaultCard(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.5f)
                                .padding(start = 5.dp),
                            filterName = "Low Pass Filter",
                            data = finalData?.lpf,
                            viewModel.lpfBuffer
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .padding(10.dp, 5.dp, 10.dp, 10.dp)
                    ) {
                        // Area2 (SAFCard | MAF Card)
                        defaultCard(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.5f)
                                .padding(end = 5.dp),
                            filterName = "Sample Average Filter",
                            data = finalData?.saf,
                            viewModel.safBuffer
                        )
                        defaultCard(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.5f)
                                .padding(start = 5.dp),
                            filterName = "Moving Average Filter",
                            data = finalData?.maf,
                            viewModel.mafBuffer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderPanel(btModel: BTModel) {
    var showScanDialog by remember { mutableStateOf(false) }

    // Create Viewmodel with ViewModelFactory
    val viewModel: BTScanViewModel = viewModel(
        factory = ViewModelFactory(btModel)
    )
    val isConnected by viewModel.isConnected.collectAsState()

    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("BT", "사용자가 블루투스를 켰습니다.")
            viewModel.ScanStart()
        } else {
            Log.d("BT", "사용자가 블루투스 켜기를 거부했습니다.")
            showScanDialog = false
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is BtUiEvent.RequestEnableBluetooth -> {
                    // 시스템 블루투스 활성화 팝업 띄우기
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    bluetoothEnableLauncher.launch(enableBtIntent)
                    Log.d("BT", "블루투스 활성화 이벤트 호출중...")
                }
            }
        }
    }

    Row {
        Image(
            modifier = Modifier.padding(3.dp).align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.fyd_logo_trans),
            contentDescription = "MainLogo"
        )
        Spacer(modifier = Modifier.weight(0.4f))
        Text(text = "Send Command: ",
            modifier = Modifier.align(Alignment.CenterVertically),
            color = Color.White
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .padding(5.dp, 10.dp, 5.dp, 10.dp)
                .align(Alignment.CenterVertically),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF0F0F0),

                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray
            ),
            shape = RoundedCornerShape(3.dp),
        )
        Spacer(modifier = Modifier.weight(0.6f))
        IconButton(
            onClick = {
                if(!isConnected) showScanDialog = true
                else viewModel.disconnectDevice() },
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(R.drawable.bt_btn_white),
                contentDescription = "Bluetooth Scan Button"
            )
        }

        if (showScanDialog) {
            BTScanDialog(
                viewModel = viewModel,
                onDismissRequest = {
                    showScanDialog = false
                },
                onDeviceSelected = { device ->
                    Log.d("SCAN", "선택된 기기: ${device.device.address}")
                    viewModel.connectDevice(device)
                    showScanDialog = false // 선택 후 창 닫기
                }
            )
        }

        IconButton(
            onClick = { /* 클릭 시 실행할 동작 */ },
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.power_btn_reversed_trans),
                contentDescription = "Connect(Power) Button"
            )
        }
        CircleDrawing(modifier = Modifier
            .fillMaxHeight()
            .padding(start = 20.dp, end = 25.dp, top = 20.dp, bottom = 20.dp),
            if (isConnected) Color.Green else Color.Red,
            radius = 18f
        )
    }
}

// Function: Remove Focus
fun Modifier.addFocusCleaner(focusManager: FocusManager): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }
}