package com.example.customerapp.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customerapp.auth.model.LoginState
import com.example.customerapp.auth.viewmodel.EmailLinkAuthViewModel
import com.example.customerapp.ui.theme.*

/**
 * Modern OTP Verification Screen
 *
 * Features:
 * - 6 separate OTP input boxes
 * - Auto-focus to next box on input
 * - Auto-focus to previous box on delete
 * - Auto-verify when all 6 digits filled
 * - 60-second countdown timer
 * - Resend OTP functionality
 * - Loading and error states
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    viewModel: EmailLinkAuthViewModel,
    onVerified: () -> Unit,
    onBack: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    val otpState by viewModel.otpState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val email by viewModel.email.collectAsState()

    // Navigate on successful verification
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Verified) {
            onVerified()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetToEmailInput()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Title
                Text(
                    text = "OTP Verification",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle with email
                Text(
                    text = "Enter the 6-digit code sent to",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // OTP Input boxes
                OtpInputBoxes(
                    otpState = otpState,
                    onOtpChange = viewModel::updateOtpDigit,
                    onOtpClear = viewModel::clearOtpDigit,
                    isLoading = loginState is LoginState.Loading
                )

                // Error message
                if (loginState is LoginState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = Color(0xFFDC3545),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Timer and resend section
                TimerAndResendSection(
                    timerState = timerState,
                    onResend = viewModel::resendOtp,
                    isLoading = loginState is LoginState.Loading
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "For Demo: Use OTP",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "123456",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            letterSpacing = 4.sp
                        )
                    }
                }
            }

            // Loading overlay
            if (loginState is LoginState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = PrimaryBlue,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

/**
 * 6 OTP Input Boxes with auto-focus behavior
 */
@Composable
private fun OtpInputBoxes(
    otpState: com.example.customerapp.auth.model.OtpState,
    onOtpChange: (Int, String) -> Unit,
    onOtpClear: (Int) -> Unit,
    isLoading: Boolean
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // Auto-focus first box on initial load
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        otpState.digits.forEachIndexed { index, digit ->
            OtpInputBox(
                value = digit,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        onOtpChange(index, newValue)
                        // Move to next box if digit entered
                        if (newValue.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                onBackspace = {
                    if (digit.isEmpty() && index > 0) {
                        // Move to previous box on backspace if current is empty
                        focusRequesters[index - 1].requestFocus()
                    } else {
                        onOtpClear(index)
                    }
                },
                focusRequester = focusRequesters[index],
                isError = false,
                enabled = !isLoading
            )
        }
    }
}

/**
 * Single OTP input box with focus handling
 */
@Composable
private fun OtpInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean,
    enabled: Boolean
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(56.dp)
            .border(
                width = 2.dp,
                color = when {
                    isError -> Color(0xFFDC3545)
                    isFocused -> PrimaryBlue
                    value.isNotEmpty() -> PrimaryBlue.copy(alpha = 0.5f)
                    else -> BorderLight
                },
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
            onValueChange = { newValue ->
                onValueChange(newValue.text)
            },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Backspace && keyEvent.type == KeyEventType.KeyDown) {
                        onBackspace()
                        true
                    } else {
                        false
                    }
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            enabled = enabled,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}

/**
 * Timer and Resend OTP section
 */
@Composable
private fun TimerAndResendSection(
    timerState: com.example.customerapp.auth.model.TimerState,
    onResend: () -> Unit,
    isLoading: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!timerState.isEnabled) {
            // Show timer countdown
            Text(
                text = "Resend OTP in ${timerState.formattedTime()}",
                fontSize = 16.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        } else {
            // Show resend button when timer expires
            Text(
                text = "Didn't receive the code?",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onResend,
                enabled = !isLoading
            ) {
                Text(
                    text = "Resend OTP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

