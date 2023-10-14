package com.example.androidkt_websocket

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.*

class MainViewModel : ViewModel() {

    private val _addressState = MutableStateFlow("192.168.43.1")
    val addressState = _addressState.asStateFlow()

    val isCorrectAddress = _addressState.map {
        it.matchWithRegex(IPV4_PATTERN)
    }

    private val _portState = MutableStateFlow("81")
    val portState = _portState.asStateFlow()

    val isCorrectPort = _portState.map {
        it.matchWithRegex(PORT_PATTERN)
    }

    val socketAddress = combine(addressState, portState) { address, port ->
        "ws://$address:$port".also {
            Log.i("MainViewModel", "socketAddress: $it")
        }
    }

    private var webSocket: WebSocket? = null

    fun connectWebSocket() {
        viewModelScope.launch {
            val ip = socketAddress.collect().toString()
            val request = Request.Builder()
                .url(ip)
                .build()

            webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    // WebSocket abierto
                    println("WebSocket abierto")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    // Mensaje recibido desde el servidor
                    println("Mensaje recibido: $text")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    // Error en la conexión del WebSocket
                    println("Error en la conexión: ${t.message}")
                }
            })

        }
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun onAddressChange(address: String) {
        _addressState.value = address
    }

    fun onPortChange(port: String) {
        _portState.value = port
    }

}

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val addressState by viewModel.addressState.collectAsState()
            val portState by viewModel.portState.collectAsState()

            val isAddressCorrect by viewModel.isCorrectAddress.collectAsState(initial = true)
            val isPortCorrect by viewModel.isCorrectPort.collectAsState(initial = true)

            val socketAddressState by viewModel.socketAddress.collectAsState("")


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                var sliderValue by remember { mutableStateOf(0f) } // El valor inicial del slider es 0

                Text(text = socketAddressState)
                AddressInput(
                    modifier = Modifier.fillMaxWidth(),
                    address = addressState,
                    port = portState,
                    isAddressCorrect = isAddressCorrect,
                    isPortCorrect = isPortCorrect,
                    onAddressChange = viewModel::onAddressChange,
                    onPortChange = viewModel::onPortChange
                )

                Button(
                    onClick = { viewModel.connectWebSocket() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Conectar al WebSocket")
                }

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = TextFieldValue(sliderValue.toInt().toString()),
                    onValueChange = {
                        // Puedes manejar la entrada del usuario aquí si es necesario
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        val percentage = (it / 180.0 * 100).toInt()
                        // Enviar el valor como un String al WebSocket
                        viewModel.sendMessage(percentage.toString())
                    },
                    valueRange = 0f..180f
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.sendMessage(sliderValue.toInt().toString()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Enviar Mensaje")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
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

val IPV4_PATTERN =
    """^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".toRegex()
val PORT_PATTERN = """^(10|[1-9][0-9]{1,3}|9999)$""".toRegex()

fun String.matchWithRegex(regex: Regex): Boolean {
    return regex.matches(this)
}
