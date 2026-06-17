package com.example.mapit.weather

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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

    // Theme Colors for "Black/Grey/Dark Vibe"
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF000000), Color(0xFF121212))
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
                        CircularProgressIndicator(color = Color.White)
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
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MapIt Weather",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.LightGray,
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
                                Icon(Icons.Default.Map, contentDescription = "Select Location", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // SLIDER SCREEN (PAGER)
                        val pagerState = rememberPagerState(pageCount = { 3 })
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.height(380.dp)
                            ) { page ->
                                when (page) {
                                    0 -> CurrentWeatherPage(state)
                                    1 -> ForecastPage(state) { selectedDay = it }
                                    2 -> OtherInfoPage(state)
                                }
                            }

                            // Pager Indicators
                            Row(
                                Modifier
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(3) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) Color.White else Color.DarkGray
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(6.dp)
                                    )
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
fun CurrentWeatherPage(state: WeatherState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        WeatherAnimation(state.current)

        Text(
            text = "${state.temp}°",
            color = Color.White,
            fontSize = 80.sp,
            fontWeight = FontWeight.ExtraLight
        )

        Text(
            text = state.current.uppercase(),
            color = Color.LightGray,
            fontSize = 16.sp,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(40.dp))

        WeatherDetailCard(state)
    }
}

@Composable
fun ForecastPage(state: WeatherState, onDayClick: (DailyForecast) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Forecast",
            color = Color.White.copy(0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.dailyForecast) { forecast ->
                ForecastItem(forecast) { onDayClick(forecast) }
            }
        }
    }
}

@Composable
fun OtherInfoPage(state: WeatherState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Statistics",
            color = Color.White.copy(0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                DetailRow("Feels Like", state.feelsLike)
                DetailRow("Pressure", state.pressure)
                DetailRow("Humidity", state.humidity)
                DetailRow("Wind", state.wind)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
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
            onMapClick = { selectedLocation = it }
        ) {
            Marker(state = markerState, title = "Selected Location")
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .background(Color(0xFF121212), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Button(
                onClick = { onLocationSelected(selectedLocation.latitude, selectedLocation.longitude) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Confirm Location")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }
}

@Composable
fun WeatherAnimation(condition: String) {
    val isSunny = condition.contains("Sunny", true)
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse),
        label = "scale"
    )

    Icon(
        imageVector = if (isSunny) Icons.Default.WbSunny else Icons.Default.Cloud,
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer(
                scaleX = scale, 
                scaleY = scale,
                rotationZ = if (isSunny) rotation else 0f
            ),
        tint = Color.White.copy(alpha = 0.9f)
    )
}

@Composable
fun ForecastItem(forecast: DailyForecast, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = forecast.day, color = Color.White, fontWeight = FontWeight.Bold)
                if (forecast.note.isNotEmpty()) {
                    Text(text = forecast.note, color = Color.LightGray, fontSize = 12.sp, maxLines = 1)
                } else {
                    Text(text = forecast.condition, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (forecast.note.isNotEmpty()) {
                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                }
                Text(text = "${forecast.temp}°", color = Color.White, fontSize = 18.sp)
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
        containerColor = Color(0xFF121212),
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Add Note", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray
                )
            )
        },
        confirmButton = {
            Button(onClick = { onSave(forecast.day, text) }, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
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
        Image(painter = painterResource(id = R.drawable.weathersnow), contentDescription = "Logo", modifier = Modifier.size(120.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Something went wrong", color = Color.White)
        Text(text = error, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 32.dp))
    }
}

@Composable
fun WeatherDetailCard(state: WeatherState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
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
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
