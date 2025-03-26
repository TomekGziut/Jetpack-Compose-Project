@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.compose

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.foundation.shape.RoundedCornerShape

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") {
            Scaffold(
                topBar = { AppTopBar() }
            ) {
                AppContent(navController)
            }
        }
        composable("heartScreen/{name}/{surname}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val surname = backStackEntry.arguments?.getString("surname") ?: ""
            DaneScreen(navController, name, surname)
        }
        composable("starScreen/{name}/{surname}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val surname = backStackEntry.arguments?.getString("surname") ?: ""
            BmiScreen(navController, name, surname)
        }
    }
}

@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text("Formularz", color = Color.White) },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.DarkGray)
    )
}

@Composable
fun AppContent(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var isDataSaved by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(color = Color.White, modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WelcomeUI(name, surname, age, height, weight,
                onNameChange = { name = it },
                onSurnameChange = { surname = it },
                onAgeChange = { age = it },
                onHeightChange = { height = it },
                onWeightChange = { weight = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (isDataSaved) {
                            navController.navigate("heartScreen/$name/$surname")
                        } else {
                            showToast(context, "Najpierw zapisz dane")
                        }
                    },
                    enabled = name.isNotEmpty() && surname.isNotEmpty() && age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0f5699))
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Dane")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dane")
                }
                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull()
                        val heightInt = height.toIntOrNull()
                        val weightInt = weight.toIntOrNull()

                        when {
                            name.isEmpty() -> showToast(context, "Imie nie może być puste")
                            surname.isEmpty() -> showToast(context, "Nazwisko nie może być puste")
                            ageInt == null || ageInt <= 0 || ageInt > 130 -> showToast(context, "Wiek musi być w przedziale (0,130]")
                            heightInt == null || heightInt !in 50..250 -> showToast(context, "Wzrost musi być w przedziale [50, 250]")
                            weightInt == null || weightInt !in 3..200 -> showToast(context, "Waga musi być w przedziale [3, 200]")
                            else -> {
                                val sharedPreferences = context.getSharedPreferences("ListaLudzi", Context.MODE_PRIVATE)
                                with(sharedPreferences.edit()) {
                                    putString("imie", name)
                                    putString("nazwisko", surname)
                                    putInt("wiek", ageInt)
                                    putInt("wzrost", heightInt)
                                    putInt("waga", weightInt)
                                    apply()
                                }
                                showToast(context, "Dane zapisane")
                                isDataSaved = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1d7835))
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Zapisz")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zapisz")
                }
                Button(
                    onClick = {
                        if (isDataSaved) {
                            navController.navigate("starScreen/$name/$surname")
                        } else {
                            showToast(context, "Najpierw zapisz dane")
                        }
                    },
                    enabled = name.isNotEmpty() && surname.isNotEmpty() && age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFcc7e00))
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "BMI")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BMI")
                }
            }
        }
    }
}

@Composable
fun WelcomeUI(
    name: String,
    surname: String,
    age: String,
    height: String,
    weight: String,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit
) {
    var selectedGender by remember { mutableStateOf("Male") }

    Text("Podaj dane...", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
    InputField(value = name, label = "Wpisz swoje imię", icon = Icons.Default.Person, onValueChange = onNameChange)
    InputField(value = surname, label = "Wpisz swoje nazwisko", icon = Icons.Default.AccountBox, onValueChange = onSurnameChange)
    InputField(value = age, label = "Wpisz swój wiek", icon = Icons.Default.DateRange, onValueChange = onAgeChange)
    InputField(value = height, label = "Wpisz swój wzrost", icon = Icons.Default.KeyboardArrowUp, onValueChange = onHeightChange)
    InputField(value = weight, label = "Wpisz swoją wagę", icon = Icons.Default.KeyboardArrowDown, onValueChange = onWeightChange)

    Text("Wybierz płeć", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedGender == "Male",
            onClick = { selectedGender = "Male" }
        )
        Text("Mężczyzna", modifier = Modifier.padding(end = 16.dp))
        RadioButton(
            selected = selectedGender == "Female",
            onClick = { selectedGender = "Female" }
        )
        Text("Kobieta", modifier = Modifier.padding(end = 16.dp))
        RadioButton(
            selected = selectedGender == "Other",
            onClick = { selectedGender = "Other" }
        )
        Text("Inna")
    }
}

@Composable
fun InputField(value: String, label: String, icon: ImageVector, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp) // Adjust the corner radius as needed
    )
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}