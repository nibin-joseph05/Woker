package com.woker.ui.alarm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun AlarmRingScreen(onSolved: () -> Unit) {

    
    val problemType = remember { Random.nextInt(1, 5) }

    var question by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf(0L) }

    
    LaunchedEffect(Unit) {
        when (problemType) {

            
            1 -> {
                val a = Random.nextInt(10, 40)
                val b = Random.nextInt(5, 25)
                val c = Random.nextInt(2, 10)
                val d = Random.nextInt(2, 10)
                question = "$a × ($b + $c) − $d²"
                correctAnswer = (a * (b + c) - (d * d)).toLong()
            }

            
            2 -> {
                val a = Random.nextInt(10, 40)
                val b = Random.nextInt(10, 40)
                val c = Random.nextInt(10, 40)
                question = "LCM($a, $b, $c)"
                correctAnswer = lcm(lcm(a, b), c).toLong()
            }

            
            3 -> {
                val a = Random.nextInt(4, 6)
                val b = Random.nextInt(4, 7)
                question = "${a}! + ${b}!"
                correctAnswer = factorial(a) + factorial(b)
            }

            
            4 -> {
                val a = Random.nextInt(50, 100)
                val b = Random.nextInt(20, 40)
                val c = Random.nextInt(10, 20)
                question = "($a × $b) mod $c"
                correctAnswer = ((a.toLong() * b.toLong()) % c.toLong())
            }
        }
    }

    var input by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⏰ Solve this to stop alarm", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(18.dp))
            Text(question, style = MaterialTheme.typography.headlineLarge)

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                isError = wrong,
                label = { Text("Your answer") }
            )

            Spacer(Modifier.height(26.dp))
            Button(
                onClick = {
                    if (input.toLongOrNull() == correctAnswer) onSolved()
                    else wrong = true
                }
            ) { Text("Submit") }

            if (wrong) {
                Text(
                    "❌ Wrong — try again!",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}


private fun lcm(a: Int, b: Int): Int {
    fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)
    return a / gcd(a, b) * b
}

private fun factorial(n: Int): Long {
    var result = 1L
    for (i in 2..n) result *= i
    return result
}
