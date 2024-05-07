package com.example.androidkt_websocket.UI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androidkt_websocket.ViewModel.MainViewModel

class UI (
) {
    @Composable
    fun MainActivityUI (
        viewModel: MainViewModel
    ) {
        val addressState by viewModel.addressState.collectAsState()
        val portState by viewModel.portState.collectAsState()
        val isAddressCorrect by viewModel.isCorrectAddress.collectAsState(initial = true)
        val isPortCorrect by viewModel.isCorrectPort.collectAsState(initial = true)
        val sliderPercent by viewModel.sliderValue.collectAsState("90")
        val current by viewModel.current.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Column {
                    Text(text = "Corriente: ${current}A")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = sliderPercent)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = sliderPercent.toFloat(),
                        onValueChange = {
                            viewModel.getSliderValue(it)
                            viewModel.sendMessage(sliderPercent)
                        },
                        valueRange = 0f..180f
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column {
                    AddressInput(
                        modifier = Modifier
                            .fillMaxWidth(),
                        address = addressState,
                        port = portState,
                        isAddressCorrect = isAddressCorrect,
                        isPortCorrect = isPortCorrect,
                        onAddressChange = viewModel::onAddressChange,
                        onPortChange = viewModel::onPortChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.connectWebSocket() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Connect")
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddressInput(
        modifier: Modifier = Modifier,
        address: String = "192.168.0.4",
        port: String = "44",
        isAddressCorrect: Boolean = true,
        isPortCorrect: Boolean = false,
        onAddressChange: (String) -> Unit = {},
        onPortChange: (String) -> Unit = {}
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(0.8f),
                value = address,
                onValueChange = onAddressChange,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    textColor = if (isAddressCorrect) Color.Black else Color.Red
                ),
                isError = !isAddressCorrect
            )

            OutlinedTextField(
                modifier = Modifier.weight(0.2f),
                value = port,
                onValueChange = onPortChange,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    textColor = if (isPortCorrect) Color.Black else Color.Red
                ),
                isError = !isPortCorrect
            )
        }
    }
}