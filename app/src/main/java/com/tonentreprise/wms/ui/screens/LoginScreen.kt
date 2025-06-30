package com.tonentreprise.wms.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.core.content.edit
import com.tonentreprise.wms.viewmodel.UserViewModel
import com.tonentreprise.wms.model.UserRole
import com.tonentreprise.wms.network.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
    }

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showWelcomeMessage by remember { mutableStateOf(false) }
    var welcomeMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Redirection automatique si token déjà existant
    LaunchedEffect(Unit) {
        val token = sharedPrefs.getString("jwt_token", null)
        val savedEmail = sharedPrefs.getString("user_email", null)

        if (!token.isNullOrEmpty() && !savedEmail.isNullOrEmpty()) {
            val isAdmin = savedEmail.startsWith("admin", ignoreCase = true)
            navController.navigate(if (isAdmin) "admin_dashboard" else "dashboard_screen") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.2f)),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Connexion",
                    fontSize = 28.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Cyan,
                        unfocusedIndicatorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Cyan
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Cyan,
                        unfocusedIndicatorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                            val authRepository = AuthRepository()
                            authRepository.loginUser(context, email.text, password.text) { success, token ->
                                if (success && token != null) {
                                    sharedPrefs.edit {
                                        putString("jwt_token", token)
                                        putString("user_email", email.text)
                                    }

                                    val role = if (email.text.startsWith("admin", ignoreCase = true)) UserRole.ADMIN else UserRole.USER
                                    userViewModel.setUserRole(role)

                                    sharedPrefs.edit {
                                        putString("user_role", if (role == UserRole.ADMIN) "admin" else "user")
                                    }

                                    coroutineScope.launch {
                                        welcomeMessage = if (role == UserRole.ADMIN)
                                            "Bonjour ADMINISTRATEUR"
                                        else
                                            "Bonjour UTILISATEUR"

                                        showWelcomeMessage = true
                                        delay(1500)
                                        showWelcomeMessage = false

                                        navController.navigate(
                                            if (role == UserRole.ADMIN) "admin_dashboard" else "dashboard_screen"
                                        ) {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                } else {
                                    welcomeMessage = "Échec de connexion"
                                    showWelcomeMessage = true
                                }
                            }
                        }
                    },
                    enabled = email.text.isNotEmpty() && password.text.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                ) {
                    Text("Se connecter", color = Color.Black)
                }

                AnimatedVisibility(visible = showWelcomeMessage, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = welcomeMessage,
                        fontSize = 20.sp,
                        color = Color.Cyan,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
