package com.woker.ui.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            Text(
                text = "⏰",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 80.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Wake Up!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Solve this problem to stop the alarm",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Question",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = question,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    wrong = false
                },
                label = { Text("Your Answer") },
                isError = wrong,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                )
            )

            if (wrong) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Incorrect! Try again",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            
            Button(
                onClick = {
                    val userAnswer = input.toLongOrNull()
                    if (userAnswer == correctAnswer) {
                        onSolved()
                    } else {
                        wrong = true
                        input = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = input.isNotEmpty()
            ) {
                Text(
                    text = "Submit Answer",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            
            Text(
                text = "You cannot dismiss this alarm until you solve the problem!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
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