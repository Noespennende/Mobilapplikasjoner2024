package com.movielist.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.R
import com.movielist.composables.ProfileImage
import com.movielist.controller.ControllerViewModel
import com.movielist.data.addUserToDatabase
import com.movielist.model.ColorModes
import com.movielist.model.Genders
import com.movielist.model.User
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.red
import com.movielist.ui.theme.textFieldColors
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular

@Composable
fun SettingsScreen (controllerViewModel: ControllerViewModel, navController: NavController){

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()
    val user = loggedInUser as User

    val handleAddImageFromPhoneClick: () -> Unit = {

    }
    val handleTakePhotoClick: () -> Unit = {

    }
    val handleBioEditedClick: (newBio: String) -> Unit = {newBio ->
        controllerViewModel.editUserBio(newBio)
    }

    val handleGenderChange: (newGender: String) -> Unit = {newGender ->
        controllerViewModel.editUserGender(newGender)
    }

    val handleWebsiteChange: (newWebsite: String) -> Unit = {newWebsite ->
        controllerViewModel.editUserWebsite(newWebsite)
    }

    val handleLocationChange: (newLocation: String) -> Unit = {newLocation ->
        controllerViewModel.editUserLocation(newLocation)
    }

    val handleAutodetectLocation: () -> Unit = {

    }
    
    val handleColorModeChange: (mode: ColorModes) -> Unit = { mode ->  
        //kontroller funksjon her
    }

    //Graphics
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsAndNavBarBackgroundHeight + 20.dp,
            bottom = bottomNavBarHeight +20.dp,
            start = horizontalPadding,
            end = horizontalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            ProfileImageSettings(
                loggedInUser = user,
                handleAddImageFromPhoneClick = handleAddImageFromPhoneClick,
                handleTakePhotoClick = handleTakePhotoClick
            )
        }

        item {
            EditGender(
                gender = user.gender,
                handleGenderChange = handleGenderChange
            )
        }

        item {
            EditBio(
                bio = user.bio,
                handleBioEditedClick = handleBioEditedClick
            )
        }

        item {
            EditWebsite(
                website = user.website,
                handleWebsiteEditedClick = handleWebsiteChange
            )
        }

        item {
            EditLocation(
                location = user.location,
                handleLocationEditedClick = handleLocationChange,
                handleAutodetectLocationClick = handleAutodetectLocation
            )
        }

        item {
            EditColorMode(
                handleColorModeChange = handleColorModeChange,
                activeColorMode = user.colorMode
            )
        }
    }
}

@Composable
fun ProfileImageSettings (
    loggedInUser: User,
    handleAddImageFromPhoneClick: () -> Unit,
    handleTakePhotoClick: () -> Unit,
    iconColor: Color = White,
    backgroundColor: Color = Purple
){

    var expanded by remember { mutableStateOf(false) }

    val handleProfileImageClick: () -> Unit = {
        expanded = !expanded
    }

    val handlePhotoClick: () -> Unit = {
        expanded = false
        handleTakePhotoClick()
    }

    val handleUploadImageClick: () -> Unit = {
        expanded = false
        handleAddImageFromPhoneClick()
    }

    val handleCancelClick: () -> Unit = {
        expanded = false
    }


    Box(
        Modifier
            .clickable {
                handleProfileImageClick()
            }
    ){
        ProfileImage(
            imageID = loggedInUser.profileImageID,
            userName = loggedInUser.userName,
            sizeMultiplier = 2.5f,
            handleProfileImageClick = handleProfileImageClick
        )

        Image(
            painter = painterResource(R.drawable.edit_filled),
            contentDescription = "Change profile image",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(100)
                )
                .padding(5.dp)
                .height(15.dp)
                .width(15.dp)
                .align(Alignment.BottomEnd)
                .clickable {
                    handleProfileImageClick()
                }
        )
    }

    ProfileImageDropDown(
        expanded = expanded,
        handleCancelClick = handleCancelClick,
        handleTakePhotoClick = handlePhotoClick,
        handleAddImageFromPhoneClick = handleUploadImageClick

    )
}


@Composable
fun ProfileImageDropDown(
    expanded: Boolean,
    handleCancelClick: () -> Unit,
    handleTakePhotoClick: () -> Unit,
    handleAddImageFromPhoneClick: () -> Unit
){

    if (expanded){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .fillMaxWidth(.7f)
                .background(
                    color = Gray,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 5.dp, vertical = 10.dp)
        ){
            //Upload Image Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        handleAddImageFromPhoneClick()
                    }
            ) {
                Text(
                    text = "Add image from phone",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            //Take a picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        handleTakePhotoClick()
                    }
            ) {
                Text(
                    text = "Take a photo",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            //Take a picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        handleCancelClick()
                    }
            ) {
                Text(
                    text = "Cancel",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }

}

@Composable
fun EditBio (
    bio: String,
    handleBioEditedClick: (updatedBio: String) -> Unit
){
    val maxCharLenght = 130
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var newBio by remember { mutableStateOf(bio) }
    var bioLenght by remember { mutableIntStateOf(maxCharLenght - newBio.length)  }

    val handleUpdateBioClick: () -> Unit = {
        if (bioLenght >= 0){
            error = false
            message = "Bio updated!"
            handleBioEditedClick(newBio)
        } else {
            error = true
            message = "Bio must be below " + maxCharLenght.toString() + " characters"
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = Gray,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(vertical = 10.dp,
                horizontal = 20.dp)
    ) {
        //Error message
        if (message.length > 0){
            Text(
                text = message,
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = if(error){red} else { White},
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 10.dp
                    )
                    .align(Alignment.CenterHorizontally)
            )
        }

        //TextField
        OutlinedTextField(
            value = newBio,
            onValueChange = {
                newBio = it
                bioLenght = maxCharLenght - newBio.length
                            },
            singleLine = false,
            colors = textFieldColors,
            textStyle = TextStyle(
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = White,
            ),
            label = {
                Text(
                    text = "Bio",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = White,
                    )
                    },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min =150.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = bioLenght.toString(),
                fontSize = paragraphSize,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                color = if(bioLenght >= 0){White} else red,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 10.dp
                    )
            )
            //Update bio button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth(.5f)
                    .clickable {
                        handleUpdateBioClick()
                    }
            ) {
                Text(
                    text = "Update bio",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }

    }

}

@Composable
fun EditGender (
    gender: String,
    handleGenderChange: (string: String) -> Unit
) {

    var dropDownExpanded by remember { mutableStateOf(false) }
    var dropDownButtonText by remember { mutableStateOf(gender) }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = Gray,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(vertical = 10.dp,
                horizontal = 20.dp)
    ){
        Text(
            text = "Gender",
            fontSize = headerSize,
            fontWeight = weightBold,
            fontFamily = fontFamily,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //Category button
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
                    .padding(vertical = 5.dp)
                    .align(Alignment.Center)
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        //dropdown menu button logic
                        dropDownExpanded = true
                    }
            ) {
                //BUTTON TEXT
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.Center)

                )

                {
                    Text(
                        text = "$dropDownButtonText",
                        fontSize = headerSize,
                        fontWeight = weightBold,
                        fontFamily = fontFamily,
                        color = DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "V",
                        fontSize = paragraphSize,
                        fontWeight = weightLight,
                        fontFamily = fontFamily,
                        color = DarkGray,
                    )

                }

                DropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = {dropDownExpanded = false},
                    offset = DpOffset(x = 30.dp, y= 0.dp),
                    modifier = Modifier
                        .background(color = DarkPurple)
                ) {
                    Genders.entries.forEach{
                            option -> DropdownMenuItem(
                        text = {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                            ){

                                Text(
                                    text = GenerateGenderText(option),
                                    fontSize = headerSize,
                                    fontWeight = weightBold,
                                    fontFamily = fontFamily,
                                    color = White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        },
                        onClick = {
                            //On click logic for dropdown menu
                            dropDownExpanded = false
                            dropDownButtonText = GenerateGenderText(option)
                            handleGenderChange(GenerateGenderText(option))
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun EditWebsite (
    website: String,
    handleWebsiteEditedClick: (updatedBio: String) -> Unit
){
    val maxCharLenght = 30
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var newWebsite by remember { mutableStateOf(website) }
    var websiteLenght by remember { mutableIntStateOf(maxCharLenght - newWebsite.length)  }

    val handleUpdateWebsiteClick: () -> Unit = {
        if (websiteLenght >= 0){
            error = false
            message = "Website updated!"
            handleWebsiteEditedClick(newWebsite)
        } else {
            error = true
            message = "Website text must be below  " + maxCharLenght.toString() + " characters"
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = Gray,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(vertical = 10.dp,
                horizontal = 20.dp)
    ) {
        //Error message
        if (message.length > 0){
            Text(
                text = message,
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = if(error){red} else { White},
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 10.dp
                    )
                    .align(Alignment.CenterHorizontally)
            )
        }

        //TextField
        OutlinedTextField(
            value = newWebsite,
            onValueChange = {
                newWebsite = it
                websiteLenght = maxCharLenght - newWebsite.length
            },
            singleLine = true,
            colors = textFieldColors,
            textStyle = TextStyle(
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = White,
            ),
            label = {
                Text(
                    text = "Website",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = White,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = websiteLenght.toString(),
                fontSize = paragraphSize,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                color = if(websiteLenght >= 0){White} else red,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 10.dp
                    )
            )
            //Update bio button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth(.5f)
                    .clickable {
                        handleUpdateWebsiteClick()
                    }
            ) {
                Text(
                    text = "Update website",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }

    }

}

@Composable
fun EditLocation (
    location: String,
    handleLocationEditedClick: (updatedBio: String) -> Unit,
    handleAutodetectLocationClick: () -> Unit
){
    val maxCharLenght = 30
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var newLocation by remember { mutableStateOf(location) }
    var locationLenght by remember { mutableIntStateOf(maxCharLenght - newLocation.length)  }

    val handleUpdateLocationClick: () -> Unit = {
        if (locationLenght >= 0){
            error = false
            message = "Location updated!"
            handleLocationEditedClick(newLocation)
        } else {
            error = true
            message = "Location text must be below  " + maxCharLenght.toString() + " characters"
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = Gray,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(vertical = 10.dp,
                horizontal = 20.dp)
    ) {
        //Error message
        if (message.length > 0){
            Text(
                text = message,
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = if(error){red} else { White},
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 10.dp
                    )
                    .align(Alignment.CenterHorizontally)
            )
        }

        //TextField
        OutlinedTextField(
            value = newLocation,
            onValueChange = {
                newLocation = it
                locationLenght = maxCharLenght - newLocation.length
            },
            singleLine = true,
            colors = textFieldColors,
            textStyle = TextStyle(
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = White,
            ),
            label = {
                Text(
                    text = "Location",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = White,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Text(
            text = locationLenght.toString(),
            fontSize = paragraphSize,
            fontFamily = fontFamily,
            fontWeight = weightLight,
            color = if(locationLenght >= 0){White} else red,
            modifier = Modifier
                .padding(
                    vertical = 10.dp,
                    horizontal = 10.dp
                )
        )

        //Autodetect button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = Purple,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .clickable {
                    handleAutodetectLocationClick()
                }
        ) {
            Text(
                text = "Autodetect",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        //Update location button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = Purple,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .clickable {
                    handleUpdateLocationClick()
                }
        ) {
            Text(
                text = "Update location",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }



    }
}

@Composable
fun EditColorMode (
    handleColorModeChange: (newMode: ColorModes) -> Unit,
    activeColorMode: ColorModes,
    activeColor: Color = Purple,
    inactiveColor: Color = LightGray
){
    var darkButtonColor by remember { mutableStateOf(
        if(activeColorMode == ColorModes.DARKMODE) { activeColor}
        else{inactiveColor}
    )
    }
    var systemButtonColor by remember { mutableStateOf(
        if(activeColorMode == ColorModes.SYSTEM) { activeColor}
        else{inactiveColor}
    ) }
    var lightuttonColor by remember { mutableStateOf(
        if(activeColorMode == ColorModes.LIGHTMODE) { activeColor}
        else{inactiveColor}
    ) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(
                color = Gray,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(vertical = 10.dp,
                horizontal = 20.dp)
    ){
        Text(
            text = "Theme",
            fontSize = headerSize,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            color = White,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),

            ) {
            //Update bio button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = darkButtonColor,
                        shape = RoundedCornerShape(
                            topStart = 5.dp,
                            bottomStart = 5.dp)
                    )
                    .fillMaxWidth(.3f)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .clickable {
                        darkButtonColor = activeColor
                        systemButtonColor = inactiveColor
                        lightuttonColor = inactiveColor
                        handleColorModeChange(ColorModes.DARKMODE)
                    }
            ) {
                Text(
                    text = "Dark",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = systemButtonColor,
                        shape = RoundedCornerShape(0.dp)
                    )
                    .fillMaxWidth(.5f)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .clickable {
                        darkButtonColor = inactiveColor
                        systemButtonColor = activeColor
                        lightuttonColor = inactiveColor
                        handleColorModeChange(ColorModes.SYSTEM)
                    }
            ) {
                Text(
                    text = "System",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            //Update bio button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = lightuttonColor,
                        shape = RoundedCornerShape(
                            topEnd = 5.dp,
                            bottomEnd = 5.dp
                        )
                    )
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .clickable {
                        darkButtonColor = inactiveColor
                        systemButtonColor = inactiveColor
                        lightuttonColor = activeColor
                        handleColorModeChange(ColorModes.LIGHTMODE)
                    }
            ) {
                Text(
                    text = "Light",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }

}




fun GenerateGenderText (gender: Genders): String {
    if (gender == Genders.PREFERNOTTOSAY){
        return "Prefer not to say"
    } else {
        return gender.toString().lowercase().replaceFirstChar { char -> char.uppercase() }
    }
}