package com.udb.comunidad_dsm.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.udb.comunidad_dsm.Home
import com.udb.comunidad_dsm.Login
import com.udb.comunidad_dsm.R
import com.udb.comunidad_dsm.isValidEmail

@Composable
fun LoginScreen(navigateTo: (route: String) -> Unit, onGoogleSignInClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("Hubo un error al iniciar sesión") }

    fun validarEmail() {
        emailError = !isValidEmail(email)
    }

    fun validarPass() {
        passwordError = password.isEmpty()
    }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_title_black),
            contentDescription = "Comunidad control logo",
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .width(200.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally)
        )
        Text(
            text = "Login",
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                validarEmail()
            },
            label = { Text("Correo") },
            isError = emailError,
            supportingText = {
                if (emailError) {
                    Text("Escriba un correo válido")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                validarPass()
            },
            label = { Text("Contraseña") },
            isError = passwordError,
            supportingText = {
                if (passwordError) {
                    Text("Contraseña es requerida")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) R.drawable.visibility
                else R.drawable.visibility_off

                // Localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                // Toggle button to hide or display password
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = description,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp)
        )

        Button(
            onClick = {
                validarEmail()
                validarPass()

                if (!emailError && !passwordError) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navigateTo(Home.route)
                            } else {
                                errorMessage =
                                    when (val exception = task.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Credenciales incorrectar o vencidas."
                                        else -> exception?.message
                                            ?: "Hubo un error la iniciar sesión."
                                    }
                                openDialog.value = true
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
        ) {
            Text("Iniciar Sesión", modifier = Modifier.padding(vertical = 5.dp))
        }

        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_g_logo_foreground),
                contentDescription = "Google logo",
                modifier = Modifier.padding(horizontal = 10.dp).width(25.dp)
            )
            Text("Iniciar con Google", modifier = Modifier.padding(vertical = 5.dp))
        }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onDismissRequest.
                    openDialog.value = false
                },
                title = { Text(text = "Error") },
                text = { Text(text = errorMessage) },
                confirmButton = {
                    TextButton(onClick = { openDialog.value = false }) { Text("Ok") }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) { Text("Cerrar") }
                }
            )
        }
    }
}