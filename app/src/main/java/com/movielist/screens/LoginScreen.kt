package com.movielist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.LogoWithName
import com.movielist.controller.ControllerViewModel
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalTextFieldColors
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.textFieldColors
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular

@Composable
fun LoginPage (controllerViewModel: ControllerViewModel, navController: NavController){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    var handleCreateUserClick: () -> Unit = {
        navController.navigate(Screen.CreateUserScreen.withArguments())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .background(
                    color = LocalColor.current.backgroundLight,
                    shape = RoundedCornerShape(5.dp)
                )
                .fillMaxWidth(.9f)
                .padding(20.dp)
        ) {
            LogoWithName()

            if (errorText.length > 0){
                //Error text
                Text(
                    text = errorText,
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.error,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }


            //Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it},
                singleLine = true,
                colors = LocalTextFieldColors.current.textFieldColors,
                textStyle = TextStyle(
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    color = LocalColor.current.secondary,
                ),
                label = {Text(
                    "email",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.secondary,
                )},
                modifier = Modifier
                    .fillMaxWidth()
            )

            //Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it},
                singleLine = true,
                colors = LocalTextFieldColors.current.textFieldColors,
                textStyle = TextStyle(
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    color = LocalColor.current.secondary,
                ),
                label = {Text(
                    "Password",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.secondary,
                )},
                modifier = Modifier
                    .fillMaxWidth()
            )

            //Login button
            Box(
                modifier = Modifier
                    .clickable {
                        //On login click logic
                        //errorText = "Wrong email or password"
                        controllerViewModel.logInWithEmailAndPassword(email, password, {
                            // Hvis innloggingen er vellykket, kaller vi callback

                            controllerViewModel.checkUserStatus()
                        }, {errorText = it})

                    }
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        color = LocalColor.current.primary,
                        shape = RoundedCornerShape(5.dp))
            ){
                Text(
                    "Login",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.background,
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                )

            }

            Text(
                "Don't have an account? Create one!",
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                textAlign = TextAlign.Center,
                color = LocalColor.current.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        handleCreateUserClick()
                    }
            )
        }
    }


}
