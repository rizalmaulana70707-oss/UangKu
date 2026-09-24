package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.BlueSecondary

@Composable
fun SplashScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BluePrimaryDark,
                        BluePrimary,
                        BlueSecondary.copy(alpha = 0.9f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Decorative background circles
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Center Branding & Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_uangku_logo),
                            contentDescription = "Logo UangKu",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // App Name
                Text(
                    text = "UangKu",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    ),
                    modifier = Modifier.testTag("splash_app_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tagline
                Text(
                    text = "Catat pengeluaran, wujudkan keuangan yang lebih baik",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mini highlight badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Smart Personal Finance",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Bottom Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol "Mulai"
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BluePrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Mulai Sekarang",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Tombol "Masuk"
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("login_nav_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Masuk ke Akun",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
