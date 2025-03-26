package com.example.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(navController: NavController, name: String, surname: String) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("ListaLudzi", Context.MODE_PRIVATE)
    val weight = sharedPreferences.getInt("waga", -1)
    val height = sharedPreferences.getInt("wzrost", -1)

    val bmi = if (height > 0 && weight > 0) {
        weight / ((height / 100.0) * (height / 100.0))
    } else {
        -1.0
    }

    val message = when {
        bmi < 18.5 -> "Hej $name, jesteś poniżej normy. Twoje BMI wynosi %.2f.".format(bmi)
        bmi in 18.5..24.9 -> "Hej $name, jesteś w świetnej formie! Twoje BMI wynosi %.2f.".format(bmi)
        bmi >= 25 -> "Hej $name, Twoje BMI wynosi %.2f, warto zadbać o zdrowie!".format(bmi)
        else -> "Nie można obliczyć BMI. Sprawdź swoje dane."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMI", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.DarkGray)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFcca362))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFcc7e00))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Ikona gwiazdki",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = message, fontSize = 24.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}