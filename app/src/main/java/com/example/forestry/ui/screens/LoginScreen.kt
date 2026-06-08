package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentType
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.forestry.R
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.utils.rainbow
import com.example.forestry.viewmodel.ForestryViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun LoginScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val emailText by viewModel.emailText.collectAsState()
    val passwordText by viewModel.passwordText.collectAsState()

    LoginScreenContent(
        emailText = emailText,
        passwordText = passwordText,
        onEmailTextChange = viewModel::onEmailTextChange,
        onPasswordTextChange = viewModel::onPasswordTextChange,
        login = viewModel::login,
        loginOffline = { viewModel.navigateTo(Screen.MAP, true) },
        modifier = modifier
    )
}

@SuppressLint("MissingPermission")
@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    emailText: String = "",
    passwordText: String = "",
    onEmailTextChange: (String) -> Unit = {},
    onPasswordTextChange: (String) -> Unit = {},
    login: () -> Unit = {},
    loginOffline: () -> Unit = {},
) {
    Surface(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Image(
                painterResource(R.drawable.icon_forest_filled),
                contentDescription = null,
                modifier = Modifier.padding(4.dp).size(128.dp)
            )
            Text(
                "Forestry",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )
            OutlinedTextField(
                emailText,
                textStyle = rainbow,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                onValueChange = onEmailTextChange,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = null
                    )
                },
                label = { Text("E-Mail") },

                modifier = Modifier
                    .padding(4.dp)
                    .semantics {
                        contentType = ContentType.EmailAddress
                    }
            )
            OutlinedTextField(
                value = passwordText,
                shape = MaterialTheme.shapes.medium,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                onValueChange = onPasswordTextChange,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null
                    )
                },
                label = { Text("Mot de passe") },
                modifier = Modifier
                    .padding(4.dp)
                    .semantics {
                        contentType = ContentType.Password
                    }
            )
            Button(
                onClick = login,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    "Se connecter",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            TextButton(
                onClick = loginOffline,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    "Continuer en mode hors ligne",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@PreviewLightDarkCombo
@Composable
fun LoginScreenPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        LoginScreenContent()
    }
}
