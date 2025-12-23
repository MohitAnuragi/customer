package com.example.customerapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customerapp.ui.theme.*
import com.example.customerapp.R


/**
 * Onboarding Screen - Matches Figma design
 * Shows sailboat illustration with "Life is short and the world is wide" message
 */
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Sailboat illustration
//            SailboatIllustration(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(350.dp)
//            )
            Image(
                painter = painterResource(id = R.drawable.first),
                contentDescription = "Sailboat illustration",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )


            Spacer(modifier = Modifier.height(40.dp))


            // Text content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main headline with "wide" in orange
                val annotatedText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    ) {
                        append("Life is short and the\nworld is ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange
                        )
                    ) {
                        append("wide")
                    }
                }

                Text(
                    text = annotatedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )

                // Subtitle
                Text(
                    text = "At Friends tours and travel, we customize\nreliable and trutworthy educational tours to\ndestinations all over the world",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Get Started button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }

            // Page indicator
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .background(PrimaryBlue, RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(4.dp)
                        .background(BorderLight, RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(4.dp)
                        .background(BorderLight, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

/**
 * Sailboat illustration matching Figma design
 */
@Composable
fun SailboatIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Sky - light blue background
        drawRect(
            color = Color(0xFFE3F2FD),
            topLeft = Offset(0f, 0f),
            size = size
        )

        // Ocean waves - darker blue
        val wavePath = Path().apply {
            moveTo(0f, height * 0.6f)
            cubicTo(
                width * 0.25f, height * 0.55f,
                width * 0.5f, height * 0.65f,
                width * 0.75f, height * 0.58f
            )
            cubicTo(
                width * 0.85f, height * 0.55f,
                width * 0.95f, height * 0.6f,
                width, height * 0.58f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = wavePath,
            color = Color(0xFF1565C0),
            style = Fill
        )

        // Wave detail - medium blue
        val waveDetailPath = Path().apply {
            moveTo(0f, height * 0.65f)
            cubicTo(
                width * 0.3f, height * 0.62f,
                width * 0.6f, height * 0.7f,
                width, height * 0.65f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = waveDetailPath,
            color = Color(0xFF1976D2),
            style = Fill
        )

        // White wave foam
        val foamPath = Path().apply {
            moveTo(width * 0.6f, height * 0.55f)
            cubicTo(
                width * 0.65f, height * 0.52f,
                width * 0.7f, height * 0.5f,
                width * 0.75f, height * 0.48f
            )
            cubicTo(
                width * 0.8f, height * 0.46f,
                width * 0.85f, height * 0.47f,
                width * 0.9f, height * 0.5f
            )
            cubicTo(
                width * 0.85f, height * 0.52f,
                width * 0.8f, height * 0.55f,
                width * 0.75f, height * 0.53f
            )
            close()
        }
        drawPath(
            path = foamPath,
            color = Color.White.copy(alpha = 0.8f),
            style = Fill
        )

        // Sailboat - white sail (main sail)
        val mainSailPath = Path().apply {
            moveTo(width * 0.35f, height * 0.65f) // Bottom of mast
            lineTo(width * 0.35f, height * 0.25f) // Top of mast
            lineTo(width * 0.25f, height * 0.55f) // Left edge of sail
            close()
        }
        drawPath(
            path = mainSailPath,
            color = Color.White,
            style = Fill
        )

        // Secondary sail - light blue
        val secondarySailPath = Path().apply {
            moveTo(width * 0.35f, height * 0.65f)
            lineTo(width * 0.35f, height * 0.3f)
            lineTo(width * 0.45f, height * 0.55f)
            close()
        }
        drawPath(
            path = secondarySailPath,
            color = Color(0xFFBBDEFB),
            style = Fill
        )

        // Boat hull - red
        val hullPath = Path().apply {
            moveTo(width * 0.25f, height * 0.65f)
            lineTo(width * 0.2f, height * 0.67f)
            quadraticBezierTo(
                width * 0.3f, height * 0.7f,
                width * 0.5f, height * 0.67f
            )
            lineTo(width * 0.45f, height * 0.65f)
            close()
        }
        drawPath(
            path = hullPath,
            color = Color(0xFFD32F2F),
            style = Fill
        )

        // Clouds
        drawCircle(
            color = Color.White,
            radius = width * 0.08f,
            center = Offset(width * 0.7f, height * 0.15f)
        )
        drawCircle(
            color = Color.White,
            radius = width * 0.06f,
            center = Offset(width * 0.75f, height * 0.17f)
        )
        drawCircle(
            color = Color.White,
            radius = width * 0.07f,
            center = Offset(width * 0.78f, height * 0.14f)
        )
    }
}

