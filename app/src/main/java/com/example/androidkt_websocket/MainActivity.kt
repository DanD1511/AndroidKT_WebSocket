package com.example.androidkt_websocket

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

class MainActivity : ComponentActivity() {
    private var webSocket: WebSocket? = null
    private val ip = "ws://192.168.43.107:81" // Reemplaza con tu dirección IP y puerto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                var sliderValue by remember { mutableStateOf(0f) } // El valor inicial del slider es 0

                Button(
                    onClick = { connectWebSocket() },
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
                        sendMessage(percentage.toString())
                    },
                    valueRange = 0f..180f
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { sendMessage(sliderValue.toInt().toString()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Enviar Mensaje")
                }
            }
        }


    }

    private fun connectWebSocket() {
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

    private fun sendMessage(message: String) {
        webSocket?.send(message)
    }
}
