package com.example.seonheum_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.seonheum_rapidrecall.ui.theme.SeonheumRapidRecallTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Purpose: The main activity for the app. It contains the userlog and switches between
 * all of the screens.
 * Design Rationale: each screen has its own composable which sends buttons clicks through callbacks
 * . All of the data is in the other classes
 * Outstanding issues: You cant back tab it will reset the data as well as phone rotation
 */

enum class Screen { Start, Game, Log, Summary }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeonheumRapidRecallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecallApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RecallApp(modifier: Modifier = Modifier) {
    val log = remember { UserLog() }
    var screen by remember { mutableStateOf(Screen.Start) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when (screen) {
            Screen.Start -> StartScreen(
                onStart = { screen = Screen.Game },
                onLog = { screen = Screen.Log },
                onSummary = { screen = Screen.Summary },
            )
            Screen.Game -> GameScreen(log, onBack = { screen = Screen.Start })
            Screen.Log -> LogScreen(log, onBack = { screen = Screen.Start })
            Screen.Summary -> SummaryScreen(UserSummary(log), onReset = { log.resetStats() }, onBack = { screen = Screen.Start })
        }
    }
}

@Composable
fun StartScreen(onStart: () -> Unit, onLog: () -> Unit, onSummary: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
            Text("RapidRecall")
            Button(onClick = onStart) { Text("Start") }
            Button(onClick = onLog) { Text("Log") }
            Button(onClick = onSummary) { Text("Attempt Summary") }
    }
}

@Composable
fun GameScreen(log: UserLog, onBack: () -> Unit) {
    var lengthText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var sequence by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showing by remember { mutableStateOf(false) }
    var shownDigit by remember { mutableStateOf("") }
    var round by remember { mutableIntStateOf(0) }
    var guess by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Attempt?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(round) {
            if (round > 0) {
                for (digit in sequence) {
                    shownDigit = digit.toString()
                    delay(1000)
                    shownDigit = ""
                    delay(300)
                }
                showing = false
            }
        }

        val finished = result
        if (finished != null) {
            Text(if (finished.isCorrect) "Correct!" else "Incorrect")
            Text("Correct sequence: ${finished.target}")
            Text("Your input: ${finished.userInput}")
            Button(onClick = {
                result = null
                sequence = emptyList()
                lengthText = ""
            }) { Text("Play Again") }
        } else if (showing) {
            Text("Memorize the digits")
            Text(shownDigit)
        } else if (sequence.isNotEmpty()) {
            Text("Enter the ${sequence.size} digits")
            OutlinedTextField(
                value = guess,
                onValueChange = { guess = it.filter { c -> c.isDigit() } },
                label = { Text("Your answer") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Button(onClick = {
                val attempt = Attempt(sequence.size, guess, sequence.joinToString(""))
                log.add(attempt)
                result = attempt
            }) { Text("Submit") }
        } else {
            OutlinedTextField(
                value = lengthText,
                onValueChange = { lengthText = it },
                label = { Text("Sequence length (1-10)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            if (error.isNotEmpty()) Text(error)
            Button(onClick = {
                val n = lengthText.toIntOrNull()
                if (n == null || n !in 1..10) {
                    error = "Enter a number from 1 to 10"
                } else {
                    error = ""
                    sequence = RandomSequence(n).generate()
                    guess = ""
                    showing = true
                    round++
                }
            }) { Text("Start Game") }
        }

        Button(onClick = onBack) { Text("Back to Start") }
    }
}

@Composable
fun LogScreen(log: UserLog, onBack: () -> Unit) {
    val format = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Log (${log.attempts.size} attempts)")
        Button(onClick = onBack) { Text("Back to Start") }
        LazyColumn {
            itemsIndexed(log.attempts) { index, a ->
                Text(
                    "Attempt ${index + 1} | Length ${a.length} | Target ${a.target} | Input ${a.userInput} | " +
                            "${if (a.isCorrect) "Correct" else "Incorrect"} | " +
                            format.format(Date(a.timestamp))
                )
            }
        }
    }
}

@Composable
fun SummaryScreen(summary: UserSummary, onReset: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Attempt Summary")
        Text("Total attempts: ${summary.total}")
        Text("Correct attempts: ${summary.correct}")
        Text("Accuracy: ${"%.1f".format(summary.accuracyPercent)}%")
        Button(onClick = onReset) { Text("Rest All Stats")}
        Button(onClick = onBack) { Text("Back to Start") }
    }
}