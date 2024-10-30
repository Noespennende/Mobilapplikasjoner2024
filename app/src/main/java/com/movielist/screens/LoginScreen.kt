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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.textFieldColors
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular

@Composable
fun LoginPage (){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf("") }

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
                    color = Gray,
                    shape = RoundedCornerShape(5.dp)
                )
                .fillMaxWidth(.9f)
                .padding(20.dp)
        ) {
            if (errorText.length > 0){
                //Error text
                Text(
                    text = errorText,
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }


            //Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it},
                singleLine = true,
                colors = textFieldColors,
                textStyle = TextStyle(
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    color = White,
                ),
                label = {Text(
                    "email",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = White,
                )},
                modifier = Modifier
                    .fillMaxWidth()
            )

            //Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it},
                singleLine = true,
                colors = textFieldColors,
                textStyle = TextStyle(
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    color = White,
                ),
                label = {Text(
                    "Password",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = White,
                )},
                modifier = Modifier
                    .fillMaxWidth()
            )

            //Login button
            Box(
                modifier = Modifier
                    .clickable {
                        //On login click logic
                        errorText = "Wrong email or password"
                    }
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp))
            ){
                Text(
                    "Login",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = DarkGray,
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                )

            }
        }
    }


}
