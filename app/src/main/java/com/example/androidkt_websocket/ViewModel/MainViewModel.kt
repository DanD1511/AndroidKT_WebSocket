package com.example.androidkt_websocket.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.androidkt_websocket.IPV4PATTERN.IPV4_DATACLASS
import com.example.androidkt_websocket.matchWithRegex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainViewModel : ViewModel() {
    private val ipv4Data = IPV4_DATACLASS()

    private val _addressState = MutableStateFlow("192.168.58.114")
    val addressState = _addressState.asStateFlow()

    val isCorrectAddress = _addressState.map {
        it.matchWithRegex(ipv4Data.iPV4PATTERN)
    }

    private val _portState = MutableStateFlow("81")
    val portState = _portState.asStateFlow()

    val isCorrectPort = _portState.map {
        it.matchWithRegex(ipv4Data.portPATTERN)
    }

    private val _sliderValue = MutableStateFlow("90")
    val sliderValue = _sliderValue.asStateFlow()

    private val _current = MutableStateFlow(0f) // Corriente calculada
    val current = _current.asStateFlow()

    val socketAddress = combine(addressState, portState) { address, port ->
        "ws://$address:$port".also {
            Log.i("MainViewModel", "socketAddress: $it")
        }
    }

    private var lastFilteredCurrent = 0f // EMA del valor anterior inicializado en 0
    private val alpha = 0.2f // Factor de suavizado para el EMA

    private var webSocket: WebSocket? = null


    fun processVoltage(voltage: String) {
        try {
            val voltageValue = voltage.toFloat()
            val offsetVoltage = 2.35f  // Valor de offset
            val sensitivity = 0.138f   // Sensibilidad
            val rawCurrent = (voltageValue - offsetVoltage) / sensitivity
            val filteredCurrent = alpha * rawCurrent + (1 - alpha) * lastFilteredCurrent // Aplica EMA
            lastFilteredCurrent = filteredCurrent // Actualiza el valor filtrado para la próxima iteración
            _current.value = filteredCurrent  // Actualiza el flujo de corriente
        } catch (e: NumberFormatException) {
            println("Error al procesar el voltaje: ${e.message}")
        }
    }

    fun connectWebSocket() {
            val addressValue = addressState.value // Recopila el valor de addressState
            val portValue = portState.value
            val socketAddressValue = "ws://$addressValue:$portValue"
        Log.i("ipAddress1", "addressValue: $addressValue")
        Log.i("ipAddress2", "portValue: $portValue")
        Log.i("ipAddress", "socketAddress: $socketAddressValue")
            val request = Request.Builder()
                .url(socketAddressValue)
                .build()

            webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    // WebSocket abierto
                    println("WebSocket abierto")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    processVoltage(text)  // Procesa el voltaje para calcular la corriente
                    //_current.value = text.toFloat()
                    println("Mensaje recibido: $text")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    // Error en la conexión del WebSocket
                    println("Error en la conexión: ${t.message}")
                }


            })


    }

    fun getSliderValue(message: Float) {
        _sliderValue.value = (message.toInt()).toString()
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