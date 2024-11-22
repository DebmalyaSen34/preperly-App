package com.example.preperly.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.preperly.ui.theme.PreperlyTheme
import com.example.preperly.ui.theme.myRed

@Composable
fun PreperlyWelcomeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(30.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Welcome to",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Preperly!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = myRed
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Preperly is the perfect platform to connect with hungry customers and boost your business.",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* Handle registration */
                        navController.navigate("Started")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = myRed)
                ) {
                    Text("Register your Restaurant")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { /* Handle login */
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = myRed)
                ) {
                    Text("Login to view your Restaurants")
                }
            }
        }
    }
}

@Preview
@Composable
fun WelcomePreview(){
    PreperlyTheme {
        val navController = rememberNavController()
        PreperlyWelcomeScreen(navController = navController)
    }
}