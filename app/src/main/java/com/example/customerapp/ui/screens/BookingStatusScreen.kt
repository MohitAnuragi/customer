package com.example.customerapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customerapp.model.BookingState
import com.example.customerapp.viewmodel.BookingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingStatusScreen(
    bookingViewModel: BookingViewModel,
    onNavigateBack: () -> Unit
) {
    val bookingState by bookingViewModel.bookingState.collectAsState()


    LaunchedEffect(bookingState) {
        if (bookingState is BookingState.Idle) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Status") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = bookingState) {
                is BookingState.Pending -> {
                    PendingContent()
                }
                is BookingState.Accepted -> {
                    AcceptedContent()
                }
                is BookingState.Rejected -> {
                    RejectedContent()
                }
                is BookingState.Error -> {
                    ErrorContent(message = state.message)
                }
                is BookingState.Idle -> {

                }
            }
        }
    }
}

@Composable
private fun PendingContent() {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(72.dp),
            strokeWidth = 6.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Processing Booking",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF333333)
        )

        Text(
            text = "Waiting for owner response...",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF666666)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Text(
                text = "Updates in real-time from Firebase",
                fontSize = 13.sp,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun AcceptedContent() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Accepted",
                modifier = Modifier.size(96.dp),
                tint = Color(0xFF4CAF50)
            )

            Text(
                text = "Booking Accepted!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Great news!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Your booking has been confirmed by the owner. You will receive further details shortly.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Text(
                text = "Returning to home...",
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun RejectedContent() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Rejected",
                modifier = Modifier.size(96.dp),
                tint = Color(0xFFF44336)
            )

            Text(
                text = "Booking Declined",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "We're sorry",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFC62828)
                    )
                    Text(
                        text = "The owner is unable to accept your booking at this time. Please try another location or time.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFC62828)
                    )
                }
            }

            Text(
                text = "Returning to home...",
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            text = "Error",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = message,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF666666)
        )
    }
}

