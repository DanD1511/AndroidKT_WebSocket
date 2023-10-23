package com.example.androidkt_websocket

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.example.androidkt_websocket.UI.UI
import com.example.androidkt_websocket.ViewModel.MainViewModel
import kotlinx.coroutines.*
import okhttp3.*

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val powerRegulator = UI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           powerRegulator.MainActivityUI(viewModel = viewModel)
        }
    }

}

fun String.matchWithRegex(regex: Regex): Boolean {
    return regex.matches(this)
}