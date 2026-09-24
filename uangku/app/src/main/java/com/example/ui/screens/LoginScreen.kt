package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: (email: String, name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailOrPhone by remember { mutableStateOf("rizalmaulana70707@gmail.com") }
    var password by remember { mutableStateOf("123456") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("Rizal Maulana") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Tombol Kembali
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("login_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Judul & Subtitle
            Text(
                text = if (isRegisterMode) "Buat Akun Baru" else "Selamat Datang",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isRegisterMode) "Daftar untuk mengelola keuangan pribadi Anda" else "Masuk untuk melanjutkan ke UangKu",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isRegisterMode) {
                // Nama Lengkap input
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama Lengkap") },
                    placeholder = { Text("Contoh: Rizal Maulana") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_full_name"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Email / No HP
            OutlinedTextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                label = { Text("Email / No. HP") },
                placeholder = { Text("user@email.com atau 0812...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_email_phone"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Kata Sandi") },
                placeholder = { Text("Masukkan kata sandi") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Lihat kata sandi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_password"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tombol "Masuk"
            Button(
                onClick = {
                    if (emailOrPhone.isBlank() || password.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Silakan lengkapi Email/No HP dan Kata Sandi")
                        }
                    } else {
                        val nameToPass = if (isRegisterMode && fullName.isNotBlank()) fullName else "Rizal Maulana"
                        onLoginSuccess(emailOrPhone, nameToPass)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_login_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                )
            ) {
                Text(
                    text = if (isRegisterMode) "Daftar Sekarang" else "Masuk",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Link "Belum punya akun? Daftar"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRegisterMode) "Sudah punya akun? " else "Belum punya akun? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isRegisterMode) "Masuk" else "Daftar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { isRegisterMode = !isRegisterMode }
                        .padding(4.dp)
                        .testTag("toggle_register_mode")
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Demo quick fill card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BlueContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Akses Cepat Pengguna Demo",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Masuk instan sebagai Rizal Maulana untuk langsung melihat data transaksi, grafik, dan laporan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            emailOrPhone = "rizalmaulana70707@gmail.com"
                            password = "demo_password"
                            fullName = "Rizal Maulana"
                            onLoginSuccess(emailOrPhone, fullName)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Masuk Instan (Rizal Maulana)")
                    }
                }
            }
        }
    }
}
