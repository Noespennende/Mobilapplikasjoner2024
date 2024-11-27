package com.movielist.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.movielist.R
import com.movielist.composables.ProfileImage
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ColorThemes
import com.movielist.model.Genders
import com.movielist.data.LocalStorageKeys
import com.movielist.model.LocationService
import com.movielist.model.User
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalConstraints
import com.movielist.ui.theme.LocalTextFieldColors
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen (controllerViewModel: ControllerViewModel, navController: NavController, localStorage: DataStore<Preferences>){


    val cameraPermission: com.google.accompanist.permissions.PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var showCameraScreen: Boolean by remember { mutableStateOf(false) }

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()
    val user = loggedInUser as User


    /* Noe med noe popup beskjed - Snackbar? */

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarStatus by controllerViewModel.snackBarStatus.collectAsState()
    val scope = rememberCoroutineScope()

    //Local storage of color theme settings
    val colorThemeState by  localStorage.data.map { it[LocalStorageKeys().colorTheme] ?: ColorThemes.DARKMODE.toString() }.collectAsState(ColorThemes.DARKMODE.toString())
    val activeColorTheme: ColorThemes = when (colorThemeState) {
        ColorThemes.DARKMODE.toString() -> {
            ColorThemes.DARKMODE
        }
        ColorThemes.SYSTEM.toString() -> {
            ColorThemes.SYSTEM
        }
        else -> {
            ColorThemes.LIGHTMODE
        }
    }


    LaunchedEffect(snackBarStatus) {
        when (snackBarStatus) {
            is ControllerViewModel.Status.Success -> {
                snackbarHostState.showSnackbar("Profilbilde oppdatert!")
            }
            is ControllerViewModel.Status.Error -> {
                val errorMessage = (snackBarStatus as ControllerViewModel.Status.Error).message
                snackbarHostState.showSnackbar("Feil: $errorMessage")
            }
            null -> { }
        }
    }

    val handleAddImageFromPhoneClick: (imageUri: Uri?) -> Unit = { imageUri ->
        if (imageUri != null) {
            controllerViewModel.handleAlbumPickProfileImage(imageUri)
        }
    }

    val handleTakePhotoClick: () -> Unit = {
        showCameraScreen = true
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

    val handleLogoutClick: () -> Unit = {
        controllerViewModel.logOutUser()
    }

    val handleColorModeChange: (theme: ColorThemes) -> Unit = { theme ->
        scope.launch {
            localStorage.edit { dataStore ->
                dataStore[LocalStorageKeys().colorTheme] = theme.toString()
            }
        }
    }

    val context = LocalContext.current

    val handleImageCapture: (image:Bitmap) -> Unit  ={image ->
        showCameraScreen = false

        controllerViewModel.handleCapturedProfileImage(context, image)

    }

    val handleCancelCameraPermissionClick: () -> Unit = {
        showCameraScreen = false
    }


    //Graphics

    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsAndNavBarBackgroundHeight + 20.dp,
            bottom = bottomNavBarHeight +20.dp,
            start = LocalConstraints.current.mainContentHorizontalPadding,
            end = LocalConstraints.current.mainContentHorizontalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        item {
            ProfileImageSettings(
                loggedInUser = user,
                handleImageAddedFromPhone = handleAddImageFromPhoneClick,
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
            )
        }

        item {
            EditColorMode(
                handleColorModeChange = handleColorModeChange,
                activeColorTheme = activeColorTheme
            )
        }

        item {
            LogOutButton (
                handleLogoutClick = handleLogoutClick
            )
        }
    }

    if(showCameraScreen){
        if(cameraPermission.status.isGranted){
            CameraScreen(
                handleImageCapture = handleImageCapture
            )
        } else {
            NoPermissionScreen(
                handleCancelClick = handleCancelCameraPermissionClick,
                handleRequestPermissionClick = cameraPermission::launchPermissionRequest
            )
        }

    }

}

@Composable
fun ProfileImageSettings (
    loggedInUser: User,
    handleImageAddedFromPhone: (uri: Uri?) -> Unit,
    handleTakePhotoClick: () -> Unit,
    iconColor: Color = LocalColor.current.secondary,
    backgroundColor: Color = LocalColor.current.backgroundLight
){

    var selectedImageUri by remember {mutableStateOf<Uri?>(null)}
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri ->
            selectedImageUri = uri
            handleImageAddedFromPhone(selectedImageUri)
        }
    )

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
        photoPicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
        handleImageAddedFromPhone(selectedImageUri)
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
                    color = LocalColor.current.backgroundLight,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 5.dp, vertical = 10.dp)
        ){
            //Upload Image Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = LocalColor.current.primary,
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
                    color = LocalColor.current.background,
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
                        color = LocalColor.current.primary,
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
                    color = LocalColor.current.background,
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
                        color = LocalColor.current.primary,
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
                    color = LocalColor.current.background,
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
    val maxCharLenght = 230
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
                color = LocalColor.current.backgroundLight,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
    ) {
        //Error message
        if (message.length > 0){
            Text(
                text = message,
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = if(error){LocalColor.current.error} else { LocalColor.current.secondary},
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
            colors = LocalTextFieldColors.current.textFieldColors,
            textStyle = TextStyle(
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = LocalColor.current.secondary,
            ),
            label = {
                Text(
                    text = "Bio",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.secondary,
                    )
                    },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
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
                color = if(bioLenght >= 0){LocalColor.current.secondary} else LocalColor.current.complimentaryThree,
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
                        color = LocalColor.current.primary,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth(.7f)
                    .clickable {
                        handleUpdateBioClick()
                    }
            ) {
                Text(
                    text = "Update bio",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = LocalColor.current.background,
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

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = LocalColor.current.backgroundLight,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
    ){
        Text(
            text = "Gender",
            fontSize = headerSize,
            fontWeight = weightBold,
            fontFamily = fontFamily,
            color = LocalColor.current.secondary,
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
                        color = LocalColor.current.primary,
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
                        color = LocalColor.current.background,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "V",
                        fontSize = paragraphSize,
                        fontWeight = weightLight,
                        fontFamily = fontFamily,
                        color = LocalColor.current.background,
                    )

                }

                DropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = {dropDownExpanded = false},
                    offset = DpOffset(x = 30.dp, y= 0.dp),
                    modifier = Modifier
                        .background(color = if(isAppInDarkTheme())LocalColor.current.tertiary else LocalColor.current.primary)
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
                                    color = if(isAppInDarkTheme()) LocalColor.current.secondary else LocalColor.current.backgroundLight,
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
    val maxCharLenght = 50
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
                color = LocalColor.current.backgroundLight,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
    ) {
        //Error message
        if (message.length > 0){
            Text(
                text = message,
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = if(error){LocalColor.current.error} else { LocalColor.current.secondary},
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
            colors = LocalTextFieldColors.current.textFieldColors,
            textStyle = TextStyle(
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = LocalColor.current.secondary,
            ),
            label = {
                Text(
                    text = "Website",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.secondary,
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
                color = if(websiteLenght >= 0){LocalColor.current.secondary} else LocalColor.current.complimentaryThree,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 10.dp
                    )
            )
            //Update website button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = LocalColor.current.primary,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth(.7f)
                    .clickable {
                        handleUpdateWebsiteClick()
                    }
            ) {
                Text(
                    text = "Update website",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = LocalColor.current.background,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }

    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditLocation (
    location: String,
    handleLocationEditedClick: (updatedLocation: String) -> Unit,
){
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val maxCharLenght = 50
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var newLocation by remember { mutableStateOf(location) }
    var locationLenght by remember { mutableIntStateOf(maxCharLenght - newLocation.length) }
    var country by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var isReceiverRegistered by remember { mutableStateOf(false) }

    val handleUpdateLocationClick: () -> Unit = {
        if (locationLenght >= 0){
            error = false
            message = "Location updated!"
            handleLocationEditedClick(newLocation)
        } else {
            error = true
            message = "Location text must be below $maxCharLenght characters"
        }
    }

    val handleAutodetectLocation: () -> Unit = {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            val startIntent = Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_START
            }
            context.startService(startIntent)
            message = "Retrieving your location..."

            if (!isReceiverRegistered) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(ctx: Context?, intent: Intent?) {
                        if (intent?.action == LocationService.LOCATION_UPDATE) {
                            country = intent.getStringExtra(LocationService.EXTRA_COUNTRY) ?: ""
                            region = intent.getStringExtra(LocationService.EXTRA_REGION) ?: ""
                            newLocation = "$region, $country"
                            message = "Location updated!"
                            error = false

                            handleLocationEditedClick(newLocation)

                            val stopIntent = Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_STOP
                            }
                            context.startService(stopIntent)


                            LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
                            isReceiverRegistered = false
                        } else {
                            message = "Ops, something went wrong. Please try again."
                            error = true
                        }
                    }
                }

                LocalBroadcastManager.getInstance(context).registerReceiver(
                    receiver,
                    IntentFilter(LocationService.LOCATION_UPDATE)
                )
                isReceiverRegistered = true
            }
        }
    }


    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(color = LocalColor.current.backgroundLight, shape = RoundedCornerShape(5.dp))
            .fillMaxWidth(0.9f)
            .padding(vertical = 10.dp, horizontal = 20.dp)
    ) {

        if (message.isNotEmpty()) {
            Text(
                text = message,
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = if (error) LocalColor.current.error else LocalColor.current.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        OutlinedTextField(
            value = newLocation,
            onValueChange = {
                newLocation = it
                locationLenght = maxCharLenght - newLocation.length
            },
            singleLine = true,
            colors = LocalTextFieldColors.current.textFieldColors,
            textStyle = TextStyle(
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = LocalColor.current.secondary,
            ),
            label = {
                Text(
                    text = "Location",
                    fontSize = headerSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = LocalColor.current.secondary,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = locationLenght.toString(),
            fontSize = paragraphSize,
            fontFamily = fontFamily,
            fontWeight = weightLight,
            color = if (locationLenght >= 0) LocalColor.current.secondary else LocalColor.current.complimentaryThree,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = LocalColor.current.primary, shape = RoundedCornerShape(5.dp))
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .clickable { handleAutodetectLocation() }
        ) {
            Text(
                text = "Autodetect",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = LocalColor.current.background,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = LocalColor.current.primary, shape = RoundedCornerShape(5.dp))
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .clickable { handleUpdateLocationClick() }
        ) {
            Text(
                text = "Update location",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = LocalColor.current.background,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun EditColorMode (
    handleColorModeChange: (newMode: ColorThemes) -> Unit,
    activeColorTheme: ColorThemes,
    activeColor: Color = LocalColor.current.primary,
    inactiveColor: Color = if(isAppInDarkTheme()) LocalColor.current.quinary else LocalColor.current.primaryLight
){
    var darkButtonColor = if(activeColorTheme == ColorThemes.DARKMODE) { activeColor} else {inactiveColor}


    var systemButtonColor = if(activeColorTheme == ColorThemes.SYSTEM) { activeColor}  else {inactiveColor}

    var lightButtonColor =  if(activeColorTheme == ColorThemes.LIGHTMODE) { activeColor}  else {inactiveColor}


    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(
                color = LocalColor.current.backgroundLight,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
    ){
        Text(
            text = "Theme",
            fontSize = headerSize,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            color = LocalColor.current.secondary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),

            ) {
            //Darkmode button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = darkButtonColor,
                        shape = RoundedCornerShape(
                            topStart = 5.dp,
                            bottomStart = 5.dp
                        )
                    )
                    .fillMaxWidth(.3f)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .clickable {
                        darkButtonColor = activeColor
                        systemButtonColor = inactiveColor
                        lightButtonColor = inactiveColor
                        handleColorModeChange(ColorThemes.DARKMODE)
                    }
            ) {
                Text(
                    text = "Dark",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = LocalColor.current.background,
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
                        lightButtonColor = inactiveColor
                        handleColorModeChange(ColorThemes.SYSTEM)
                    }
            ) {
                Text(
                    text = "System",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = LocalColor.current.background,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            //Lightmode button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = lightButtonColor,
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
                        lightButtonColor = activeColor
                        handleColorModeChange(ColorThemes.LIGHTMODE)
                    }
            ) {
                Text(
                    text = "Light",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = LocalColor.current.background,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }

}

@Composable
fun LogOutButton (
    handleLogoutClick: () -> Unit
) {

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = LocalColor.current.backgroundLight,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(.9f)
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = LocalColor.current.primary, shape = RoundedCornerShape(5.dp))
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .clickable { handleLogoutClick() }
        ) {
            Text(
                text = "Sign out",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = LocalColor.current.background,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
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

