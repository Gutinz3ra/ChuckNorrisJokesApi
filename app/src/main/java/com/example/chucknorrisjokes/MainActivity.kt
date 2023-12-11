package com.example.chucknorrisjokes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { ApiService }
    viewModel { JokeViewModel(get()) }
}

@Composable
fun JokeScreen() {
    val viewModel: JokeViewModel = viewModel()
    val joke by viewModel.joke.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = joke, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { viewModel.fetchNewJoke() }) {
            Text(text = "Get New Joke")
        }
    }
}

class JokeViewModel(private val apiService: ApiService) : ViewModel() {
    private val _joke = MutableStateFlow("Loading joke...")
    val joke: StateFlow<String> = _joke.asStateFlow()


fun fetchNewJoke() {
    viewModelScope.launch {
        try {
            val response = ApiService.chuckNorrisApi.getRandomJoke()
            if (response.isSuccessful) {
                _joke.value = response.body()?.value ?: "No joke found."
            } else {
                _joke.value = "Error: ${response.message()}"
            }
        } catch (t: Throwable) {
            _joke.value = "Exception: ${t.message}"
        }
    }
}

    init {
        fetchNewJoke()
    }
}