package com.example.chucknorrisjokes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chucknorrisjokes.ui.theme.ChuckNorrisJokesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChuckNorrisJokesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JokeScreen()
                }
            }
        }
    }
}

@Composable
fun JokeScreen() {
    val viewModel: JokeViewModel = viewModel()

    val joke by viewModel.joke.observeAsState("")

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


class JokeViewModel : ViewModel() {
    private val _joke = MutableLiveData<String>()
    val joke: LiveData<String> = _joke

    fun fetchNewJoke() {
        viewModelScope.launch {
            try {
                val response = ApiService.chuckNorrisApi.getRandomJoke()
                if (response.isSuccessful) {
                    _joke.value = response.body()?.value ?: "No joke found."
                } else {
                    _joke.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _joke.value = "Exception: ${e.message}"
            }
        }
    }

    init {
        fetchNewJoke()
    }
}
