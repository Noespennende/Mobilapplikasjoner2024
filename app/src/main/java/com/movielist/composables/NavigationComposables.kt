package com.movielist.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.movielist.R
import com.movielist.model.NavbarOptions
import com.movielist.model.TopNavDropdownOptions
import com.movielist.model.User
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.bottomPhoneIconsOffset
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBaHeight
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.weightBold





@Composable
fun BottomNavBar(
    activeColor: Color = LocalColor.current.primary,
    inactiveColor: Color = LocalColor.current.quaternary,
    sizeMultiplier: Float = 1f,
    activeNavButton: NavbarOptions,
    handleNavButtonClick: (NavbarOptions) -> Unit
){
    //Graphics variables
    val buttonSize: Dp = (45*sizeMultiplier).dp
    val iconSize: Dp = buttonSize-20.dp


    var homeButtonColor= inactiveColor
    var listButtonColor = inactiveColor
    var searchButtonColor = inactiveColor
    var reviewButtonColor = inactiveColor
    var profileButtonColor = inactiveColor

    if (activeNavButton == NavbarOptions.HOME){
        homeButtonColor = activeColor
    } else if (activeNavButton == NavbarOptions.LIST){
        listButtonColor = activeColor
    } else if (activeNavButton == NavbarOptions.PROFILE){
        profileButtonColor = activeColor
    } else if (activeNavButton == NavbarOptions.REVIEW){
        reviewButtonColor = activeColor
    } else if (activeNavButton == NavbarOptions.SEARCH) {
        searchButtonColor = activeColor
    }


    //wrapper
    Box (
        modifier = Modifier.fillMaxSize()
    )
    {
        //Navbar content
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = bottomPhoneIconsOffset -10.dp,
                    start = horizontalPadding -10.dp,
                    end = horizontalPadding -10.dp
                )
        ) {
            //Home button
            Button(
                onClick = {
                    //Home button onClick logic
                    if(activeNavButton != NavbarOptions.HOME){
                        handleNavButtonClick(NavbarOptions.HOME)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    //Home button icon
                    Image(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(homeButtonColor),
                        modifier = Modifier
                            .size(iconSize)
                    )
                    //Home button text
                    Text(
                        text = "Home",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        fontSize = paragraphSize,
                        color = homeButtonColor
                    )
                }
            }

            //List button
            Button(
                onClick = {
                    //List button onClick logic
                    if(activeNavButton != NavbarOptions.LIST){
                        handleNavButtonClick(NavbarOptions.LIST)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //List button icon
                    Image(
                        painter = painterResource(id = R.drawable.list),
                        contentDescription = "List",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(listButtonColor),
                        modifier = Modifier
                            .size(iconSize)
                    )

                    //List button text
                    Text(
                        text = "List",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        fontSize = paragraphSize,
                        color = listButtonColor
                    )
                }

            }

            //Search button
            Button(
                onClick = {
                    //Search button onClick logic
                    if(activeNavButton != NavbarOptions.SEARCH){
                        handleNavButtonClick(NavbarOptions.SEARCH)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Search button icon
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(searchButtonColor),
                        modifier = Modifier
                            .size(iconSize)
                    )

                    //Search button text
                    Text(
                        text = "Search",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        fontSize = paragraphSize,
                        color = searchButtonColor
                    )
                }

            }

            //Review button
            Button(
                onClick = {
                    //Review button onClick logic
                    if(activeNavButton != NavbarOptions.REVIEW){
                        handleNavButtonClick(NavbarOptions.REVIEW)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Review button icon
                    Image(
                        painter = painterResource(id = R.drawable.review),
                        contentDescription = "Review",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(reviewButtonColor),
                        modifier = Modifier
                            .size(iconSize)
                    )

                    //Review button text
                    Text(
                        text = "Reviews",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        fontSize = paragraphSize,
                        color = reviewButtonColor
                    )
                }
            }

            //Profile button
            Button(
                onClick = {
                    //Profile button onClick logic
                    if(activeNavButton != NavbarOptions.PROFILE){
                        handleNavButtonClick(NavbarOptions.PROFILE)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Profile button icon
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Review",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(profileButtonColor),
                        modifier = Modifier
                            .size(iconSize)
                    )

                    //profile button text
                    Text(
                        text = "Profile",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        fontSize = paragraphSize,
                        color = profileButtonColor
                    )
                }
            }
        }
    }


}


@Composable
fun BottomNavbarAndMobileIconsBackground (
    color: Color = LocalColor.current.backgroundLight,
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height(bottomNavBarHeight)
                .align(Alignment.BottomCenter)
        )
    }

}

@Composable
fun TopScreensNavbarBackground (
    color: Color = LocalColor.current.backgroundLight,
    sizeMultiplier: Float = 1f
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height((topPhoneIconsAndNavBarBackgroundHeight + topNavBaHeight)*sizeMultiplier)
                .align(Alignment.TopCenter)
        )
    }

}


@Composable
fun TopNav (
    loggedInUser: User?,
    CurrentScreen: String,
    handleProfileClick: () -> Unit,
    handleSettingsClick: () -> Unit,
    handleLogoClick: () -> Unit
){

    var dropDownExpanded by remember { mutableStateOf(false) }

    val handleDropDownButtonClick: () -> Unit = {
        dropDownExpanded = !dropDownExpanded
    }

    val handleDropDownCategoryClick: (option: TopNavDropdownOptions) -> Unit = {option ->
        if (option == TopNavDropdownOptions.PROFILE){
            dropDownExpanded = false
            handleProfileClick()
        } else if ( option == TopNavDropdownOptions.SETTINGS){
            dropDownExpanded = false
            handleSettingsClick()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        TopNavBackground()
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(topPhoneIconsAndNavBarBackgroundHeight)
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = horizontalPadding, end = horizontalPadding)
        )
        {
            Logo(
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp)
                    .clickable {
                        handleLogoClick()
                    }
            )
            Text(
                text = CurrentScreen,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = headerSize,
                textAlign = TextAlign.Center,
                color = LocalColor.current.secondary,
                modifier = Modifier
                )

            if (loggedInUser != null){
                Box{
                    ProfileImage(
                        imageID = loggedInUser.profileImageID,
                        userName = loggedInUser.userName,
                        handleProfileImageClick = handleDropDownButtonClick,
                        sizeMultiplier = .8f
                    )

                    TopNavDropDown(
                        expanded = dropDownExpanded,
                        handleDismiss = handleDropDownButtonClick,
                        handleClick = handleDropDownCategoryClick
                    )
                }

            }

        }

    }

}

@Composable
fun TopNavBackground (
    color: Color = LocalColor.current.backgroundDark,
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height(topPhoneIconsAndNavBarBackgroundHeight)
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
fun TopNavDropDown(
    expanded: Boolean,
    handleDismiss: () -> Unit,
    handleClick: (TopNavDropdownOptions) -> Unit
){
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {handleDismiss()},
        offset = DpOffset(x = 0.dp, y= 0.dp),
        modifier = Modifier
            .background(color = if(isAppInDarkTheme())LocalColor.current.tertiary else LocalColor.current.primary)
            .width(100.dp)
    ) {
        TopNavDropdownOptions.entries.forEach(){
                option -> DropdownMenuItem(
            text = {
                Box(modifier = Modifier
                    .fillMaxWidth()
                ){
                    //MENU ITEM TEXT
                    Text(
                        text = option.toString().lowercase().replaceFirstChar { char -> char.uppercase() },
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
                handleClick(option)
            })
        }
    }
}


