package com.example.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class GenderApiResponse(
    val name: String,
    val gender: String?,
    val probability: Float,
    val count: Int
)

interface GenderApiService {
    @GET("https://api.genderize.io")
    suspend fun getGender(@Query("name") name: String): Response<GenderApiResponse>
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaneScreen(navController: NavController, name: String, surname: String) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("ListaLudzi", Context.MODE_PRIVATE)
    val imie = sharedPreferences.getString("imie", "Brak danych") ?: "Brak danych"
    val nazwisko = sharedPreferences.getString("nazwisko", "Brak danych") ?: "Brak danych"
    val wiek = sharedPreferences.getInt("wiek", -1)
    val wzrost = sharedPreferences.getInt("wzrost", -1)
    val waga = sharedPreferences.getInt("waga", -1)

    var gender by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun translateGender(gender: String?): String {
        return when (gender) {
            "male" -> "mężczyzna"
            "female" -> "kobieta"
            else -> "inna"
        }
    }

    LaunchedEffect(name) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.genderize.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GenderApiService::class.java)

        scope.launch {
            try {
                val response = service.getGender(name)
                if (response.isSuccessful) {
                    gender = translateGender(response.body()?.gender)
                } else {
                    errorMessage = "Failed to fetch gender"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dane Urzytkownika", color = Color.White) },
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
                .background(Color(0xFF466e94))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red)
                } else {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0f5699))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Ikona serca",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Imię: $imie", fontSize = 20.sp, color = Color.White)
                            Text(text = "Nazwisko: $nazwisko", fontSize = 20.sp, color = Color.White)
                            Text(text = "Wiek: ${if (wiek != -1) wiek else "Brak danych"}", fontSize = 20.sp, color = Color.White)
                            Text(text = "Wzrost: ${if (wzrost != -1) wzrost else "Brak danych"}", fontSize = 20.sp, color = Color.White)
                            Text(text = "Waga: ${if (waga != -1) waga else "Brak danych"}", fontSize = 20.sp, color = Color.White)
                            Text(text = "Płeć: ${gender ?: "Nieznana"}", fontSize = 20.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}