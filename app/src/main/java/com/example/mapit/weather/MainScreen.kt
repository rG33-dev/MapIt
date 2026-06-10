package com.example.mapit.weather

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapit.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MainScreen(viewModel: WeatherViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedDay by remember { mutableStateOf<DailyForecast?>(null) }
    var showMap by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        if (showMap) {
            MapPicker(
                initialLat = state.latitude,
                initialLng = state.longitude,
                onLocationSelected = { lat, lng ->
                    viewModel.updateLocation(lat, lng, "Selected Location")
                    showMap = false
                },
                onClose = { showMap = false }
            )
        } else {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.Cyan)
                    }
                }
                state.error != null -> {
                    ErrorPlaceholder(state.error!!)
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MapIt Weather",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.Cyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = state.locationName,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            IconButton(onClick = { showMap = true }) {
                                Icon(Icons.Default.Map, contentDescription = "Select Location", tint = Color.Cyan)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        WeatherAnimation(state.current)

                        Text(
                            text = "${state.temp}°",
                            color = Color.White,
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Thin
                        )

                        Text(
                            text = state.current.uppercase(),
                            color = Color.Cyan,
                            fontSize = 16.sp,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Light
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        WeatherDetailCard(state)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Daily Forecast (Tap to add note)",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.dailyForecast) { forecast ->
                                ForecastItem(forecast) {
                                    selectedDay = forecast
                                }
                            }
                        }
                    }
                }
            }
        }

        // Note Dialog
        selectedDay?.let { forecast ->
            NoteEditDialog(
                forecast = forecast,
                onDismiss = { selectedDay = null },
                onSave = { day, note ->
                    viewModel.updateNote(day, note)
                    selectedDay = null
                }
            )
        }
    }
}

@Composable
fun MapPicker(
    initialLat: Double,
    initialLng: Double,
    onLocationSelected: (Double, Double) -> Unit,
    onClose: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf(LatLng(initialLat, initialLng)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 10f)
    }
    val markerState = rememberMarkerState(position = selectedLocation)

    LaunchedEffect(selectedLocation) {
        markerState.position = selectedLocation
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                selectedLocation = it
            }
        ) {
            Marker(
                state = markerState,
                title = "Selected Location"
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .background(Color(0xFF1E293B).copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Button(
                onClick = { onLocationSelected(selectedLocation.latitude, selectedLocation.longitude) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
            ) {
                Text("Confirm Location")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun WeatherAnimation(condition: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(
        modifier = Modifier
            .size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (condition.contains("Sunny", true)) Icons.Default.WbSunny else Icons.Default.Cloud,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale),
            tint = if (condition.contains("Sunny", true)) Color(0xFFFFD700) else Color.LightGray
        )
    }
}

@Composable
fun ForecastItem(forecast: DailyForecast, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = forecast.day, color = Color.White, fontWeight = FontWeight.Bold)
                if (forecast.note.isNotEmpty()) {
                    Text(
                        text = forecast.note,
                        color = Color.Cyan.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                } else {
                    Text(text = forecast.condition, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (forecast.note.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(14.dp).padding(end = 4.dp)
                    )
                }
                Text(text = "${forecast.temp}°", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditDialog(forecast: DailyForecast, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var text by remember { mutableStateOf(forecast.note) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Note for ${forecast.day}", color = Color.White) },
        containerColor = Color(0xFF1E293B),
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("What's happening?") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(forecast.day, text) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        }
    )
}

@Composable
fun ErrorPlaceholder(error: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.weathersnow),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Oops! Something went wrong", color = Color.White, fontWeight = FontWeight.Bold)
        Text(
            text = error,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun WeatherDetailCard(state: WeatherState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherStat("Humidity", state.humidity)
            VerticalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.height(30.dp))
            WeatherStat("Wind Speed", state.wind)
        }
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
