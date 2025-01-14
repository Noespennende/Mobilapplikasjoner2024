package com.movielist.screens

import android.util.Log
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.R
import com.movielist.Screen
import com.movielist.composables.HamburgerButton
import com.movielist.composables.LineDevider
import com.movielist.composables.ListItemListSidesroller
import com.movielist.composables.ProfileImage
import com.movielist.composables.ProgressBar
import com.movielist.composables.RoundProgressBar
import com.movielist.composables.SettingsButton
import com.movielist.composables.TopScreensNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.FollowStatus
import com.movielist.model.ListItem
import com.movielist.model.ProductionType
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalConstraints
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.isAppInPortraitMode
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ProfilePage (controllerViewModel: ControllerViewModel, navController: NavController, userID: String?){

    // TEMP CODE DELETE BELOW
    val exampleUser: User = User(
        id = "IDfromFirebase",
        userName = "Example User",
        email = "Example@Email.com",
    )

    val reviewList = mutableListOf<ReviewDTO>()



    val handleProductionClick: (productionID: String, productionType: ProductionType)
    -> Unit = { productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType.name))
    }


    // TEMP CODE DELETE ABOVE

    val profileOwnerID by remember { mutableStateOf(userID) } /* <- ID of the user that owns the profile we are looking at*/

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()

    val profileOwner = controllerViewModel.profileOwner.collectAsState().value

    val profileOwnerLoaded = remember { mutableStateOf(false) }

    var profileBelongsToLoggedInUser by remember { mutableStateOf(true) } /* <-- Kontroller funksjon som gir bolean verdi true/false basert på om dette stemmer*/

    val profileOwnersReviews = remember { mutableStateOf<List<ReviewDTO>>(emptyList()) }/*<- List of reviews by the profile owner,  replace with list gotten by controller*/

    val usersFavoriteMovies = controllerViewModel.getUsersFavoriteMovies(profileOwner)

    val usersFavoriteTVShows = controllerViewModel.getUsersFavoriteTVShows(profileOwner)

    val userFollowerCount = controllerViewModel.getUserFollowingCount()

    val userFollowingMeCount = controllerViewModel.getUsersFollowingMeCount()

    val userMovieCount = controllerViewModel.getMovieInListsCount()

    val userTvShowCount = controllerViewModel.getShowsInListsCount()

    var activeTab by remember { mutableStateOf(com.movielist.model.ProfileCategoryOptions.SUMMARY) }

    var followStatus: FollowStatus by remember { mutableStateOf(FollowStatus.NOTFOLLOWING) }

    LaunchedEffect(profileOwner, loggedInUser) {
        if (profileOwner != null && loggedInUser != null) {

            followStatus = controllerViewModel.determineFollowStatus()
        }
    }
    //function variables:
    val user = profileOwner ?: exampleUser

    val isLoggedInUser by remember {
        mutableStateOf(true)
    }

    val handleReviewButtonLikeClick: (reviewID: String, productionType: ProductionType) -> Unit = { reviewID, productionType ->
        //Kontroller funksjon her
    }

    val handleProfilePictureClick: (profileID: String) -> Unit = { profileID ->
        navController.navigate(Screen.ProfileScreen.withArguments(profileID))
    }

    val handleReviewClick: (reviewID: String) -> Unit = { reviewID ->
        navController.navigate(Screen.ReviewScreen.withArguments(reviewID))
    }

    val handleSummaryClick: () -> Unit = {
        activeTab = com.movielist.model.ProfileCategoryOptions.SUMMARY
    }
    val handleLibraryClick: () -> Unit = {
        activeTab = com.movielist.model.ProfileCategoryOptions.LIBRARY
        navController.navigate(Screen.ListScreen.withArguments(profileOwnerID.toString()))
    }
    val handleReviewsClick: () -> Unit = {
        activeTab = com.movielist.model.ProfileCategoryOptions.REVIEWS
    }

    val handleSettingsButtonClick: () -> Unit = {
        navController.navigate(Screen.SettingsScreen.withArguments())
    }

    val handleFollowUnfollowClick: (newFollowStatus: FollowStatus) -> Unit = { newFollowStatus ->
        val profileOwnerUser = profileOwner
        if (profileOwnerUser != null) {
            if (newFollowStatus == FollowStatus.FOLLOWING) {
                controllerViewModel.addUserToFollowerList(profileOwnerUser)
            } else if (newFollowStatus == FollowStatus.NOTFOLLOWING) {
                controllerViewModel.removeUserFromFollowerList(profileOwnerUser)
            }
        }
    }


    LaunchedEffect(userID) {
        profileOwnerLoaded.value = false

        profileOwnersReviews.value = emptyList()
        if (userID != null) {
            controllerViewModel.loadProfileOwner(userID)

            profileOwnerLoaded.value = true
        }
    }

    LaunchedEffect(profileOwnerLoaded.value) {


        if (profileOwnerLoaded.value) {

            profileOwnersReviews.value = emptyList()
            profileOwner?.let { owner ->

                profileBelongsToLoggedInUser = owner.id == loggedInUser?.id

                val reviews = controllerViewModel.getUsersReviews(owner)

                profileOwnersReviews.value = reviews

            }
        }
    }

    //Graphics
    //Main Content
    LazyColumn(
        contentPadding = PaddingValues(
            top = LocalConstraints.current.mainContentStart + 10.dp,
            bottom = LocalConstraints.current.bottomUniversalNavbarHeight + 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    )
    {
        if(activeTab == com.movielist.model.ProfileCategoryOptions.SUMMARY){

            item {
                if(!isAppInPortraitMode()) {
                    UsernameHeadline(user = user)
                }
            }

            //Biosection
            item{
                //Wrapper for horisontal padding
                Box(
                    modifier = Modifier
                        .padding(
                            start = LocalConstraints.current.mainContentHorizontalPadding,
                            end = LocalConstraints.current.mainContentHorizontalPaddingAlternative
                        )
                )
                {
                    ProfileInfoSection(
                        user = user,
                        followStatus = followStatus ,
                        loggedInUsersProfile = profileBelongsToLoggedInUser,
                        handleSettingsButtonClick = handleSettingsButtonClick,
                        handleFollowUnfollowClick = handleFollowUnfollowClick,
                        followingCount = userFollowerCount,
                        followersCount = userFollowingMeCount,
                        tvShowCount = userTvShowCount,
                        userMovieCont = userMovieCount

                    )
                }

            }

            //Favorite Series
            item {
                ListItemListSidesroller(
                    header = "Favorite series",
                    listOfShows = usersFavoriteTVShows,
                    handleImageClick = handleProductionClick,
                    textModifier = Modifier
                        .padding(
                            start = LocalConstraints.current.mainContentHorizontalPadding,
                            bottom = 15.dp
                        )

                )
            }

            //Line devider
            item {
                //Wrapper for the line devider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = LocalConstraints.current.mainContentHorizontalPadding, end = LocalConstraints.current.mainContentHorizontalPaddingAlternative)
                ){
                    LineDevider()
                }

            }

            //Favorite Movies
            item {
                ListItemListSidesroller(
                    header = "Favorite movies",
                    listOfShows = usersFavoriteMovies,
                    handleImageClick = handleProductionClick,
                    textModifier = Modifier
                        .padding(
                            start = LocalConstraints.current.mainContentHorizontalPadding,
                            bottom = 15.dp
                        )
                )
            }

            //Line devider
            item {
                //Wrapper for the line devider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = LocalConstraints.current.mainContentHorizontalPadding, end = LocalConstraints.current.mainContentHorizontalPaddingAlternative)
                ){
                    LineDevider()
                }

            }

            //Statistics section
            item {
                StatisticsSection(

                    showGenreToPercentageMap = controllerViewModel.genrePercentageShows(),
                    movieGenreToPercentageMap = controllerViewModel.genrePercentageMovie()
                )
            }

            //Line devider
            item {
                //Wrapper for the line devider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = LocalConstraints.current.mainContentHorizontalPadding, end = LocalConstraints.current.mainContentHorizontalPaddingAlternative)
                ){
                    LineDevider()
                }

            }

            //Review section
            item {
                ReviewsSection(
                    reviewList = profileOwnersReviews.value,
                    header = "Reviews",
                    handleLikeClick = handleReviewButtonLikeClick,
                    handleProductionImageClick = handleProductionClick,
                    handleProfilePictureClick = handleProfilePictureClick,
                    handleReviewClick = handleReviewClick,
                    paddingEnd = LocalConstraints.current.mainContentHorizontalPaddingAlternative,
                    loggedInUser = loggedInUser ?: User("","","")

                )
            }
        }
        else if (activeTab == com.movielist.model.ProfileCategoryOptions.REVIEWS) {
            item {
                ReviewsSection(
                    reviewList = profileOwnersReviews.value,
                    header = "Reviews by " + user.userName,
                    handleLikeClick = handleReviewButtonLikeClick,
                    handleReviewClick = handleReviewClick,
                    handleProfilePictureClick = handleProfilePictureClick,
                    handleProductionImageClick = handleProductionClick,
                    paddingEnd = LocalConstraints.current.mainContentHorizontalPaddingAlternative,
                    loggedInUser = loggedInUser ?: User("","","")
                )
            }
        }
    }

    if (isAppInPortraitMode()){
        //Navigation
        TopNavBarProfilePage(
            user = user,
            handleSummaryClick = handleSummaryClick,
            handleLibraryClick = handleLibraryClick,
            handleReviewsClick = handleReviewsClick
        )
    } else {
        TopNavigationProfileScreenLandscape(
            handleSummaryClick = handleSummaryClick,
            handleLibraryClick = handleLibraryClick,
            handleReviewsClick = handleReviewsClick
        )
    }

}

@Composable
fun TopNavBarProfilePage(
    user: User,
    handleSummaryClick: () -> Unit,
    handleLibraryClick: () -> Unit,
    handleReviewsClick: () -> Unit
) {
    TopScreensNavbarBackground()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        UsernameHeadline(
            user = user
        )

        ProfileCategoryOptions(
            handleSummaryClick = handleSummaryClick,
            handleLibraryClick = handleLibraryClick,
            handleReviewsClick = handleReviewsClick
        )

    }

}

@Composable
fun UsernameHeadline (
    user: User
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if(isAppInPortraitMode())LocalConstraints.current.topLocalScreenNavbarContentStart else 8.dp )
    ){
        ProfileImage(
            imageID = user.profileImageID,
            userName = user.userName,
            sizeMultiplier = .8f
        )

        Text(
            text = user.userName,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = LocalColor.current.secondary,
            modifier = Modifier
                .padding(horizontal = 10.dp)
        )
    }
}

@Composable
fun ProfileCategoryOptions(
    activeButtonColor: Color = LocalColor.current.primary,
    inactiveButtonColor: Color = if(isAppInDarkTheme())LocalColor.current.quaternary else LocalColor.current.primaryLight,
    handleSummaryClick: () -> Unit,
    handleLibraryClick: () -> Unit,
    handleReviewsClick: () -> Unit,
){

    var activeButton by remember {
        mutableStateOf(com.movielist.model.ProfileCategoryOptions.SUMMARY)
    }
    //Button graphics logic
    var summaryButtonColor = if(activeButton == com.movielist.model.ProfileCategoryOptions.SUMMARY) activeButtonColor else inactiveButtonColor
    var libaryButtonColor = if(activeButton == com.movielist.model.ProfileCategoryOptions.LIBRARY) activeButtonColor else inactiveButtonColor
    var reviewsButtonColor  = if(activeButton == com.movielist.model.ProfileCategoryOptions.REVIEWS) activeButtonColor else inactiveButtonColor



    //Graphics
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = LocalConstraints.current.mainContentHorizontalPadding)
    ){
        item {
            //Summary
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != com.movielist.model.ProfileCategoryOptions.SUMMARY) {
                            activeButton = com.movielist.model.ProfileCategoryOptions.SUMMARY
                            handleSummaryClick()
                        }
                    }
                    .background(
                        color = summaryButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Summary",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }

        item {
            //Completed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != com.movielist.model.ProfileCategoryOptions.LIBRARY) {

                            activeButton = com.movielist.model.ProfileCategoryOptions.LIBRARY
                            handleLibraryClick()
                        }
                    }
                    .background(
                        color = libaryButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Library",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }

        item {
            //Want to watch
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != com.movielist.model.ProfileCategoryOptions.REVIEWS) {

                            activeButton = com.movielist.model.ProfileCategoryOptions.REVIEWS
                            handleReviewsClick()
                        }

                    }
                    .background(
                        color = reviewsButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Reviews",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }
    }
}

@Composable
fun ProfileInfoSection (
    user: User,
    loggedInUsersProfile: Boolean,
    followStatus: FollowStatus,
    handleSettingsButtonClick: () -> Unit,
    handleFollowUnfollowClick: (followStatus: FollowStatus) -> Unit,
    followingCount: Int,
    followersCount: Int,
    userMovieCont: Int,
    tvShowCount: Int
){


    val primaryColor = LocalColor.current.primary
    val backgroundColor = LocalColor.current.quaternary

    var followButtonColor by remember { mutableStateOf(
        if (followStatus == FollowStatus.NOTFOLLOWING){
            primaryColor
        } else {
            backgroundColor
        }
    ) }

    var newFollowStatus by remember { mutableStateOf(followStatus) }

    LaunchedEffect(followStatus) {
        newFollowStatus = followStatus
        followButtonColor = if (newFollowStatus == FollowStatus.NOTFOLLOWING) primaryColor else backgroundColor
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
    ){
        if (loggedInUsersProfile){
            SettingsButton(
                handleSettingsButtonClick = handleSettingsButtonClick,
                filled = true,
                sizeMultiplier = 1.0f,
                backgroundColor = LocalColor.current.backgroundLight,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 10.dp)
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(bottom = 15.dp)
            ){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable {
                            if (newFollowStatus == FollowStatus.NOTFOLLOWING){
                                newFollowStatus = FollowStatus.FOLLOWING
                                followButtonColor = backgroundColor
                                handleFollowUnfollowClick(newFollowStatus)
                            } else {
                                newFollowStatus = FollowStatus.NOTFOLLOWING
                                followButtonColor = primaryColor
                                handleFollowUnfollowClick(newFollowStatus)
                            }
                        }
                        .background(
                            color = followButtonColor,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(150.dp)
                        .height(30.dp)
                ) {
                    Text(
                        text = if (newFollowStatus == FollowStatus.NOTFOLLOWING){"Follow"} else {"Unfollow"},
                        fontSize = paragraphSize,
                        fontWeight = weightBold,
                        color = LocalColor.current.background
                    )
                }
            }
        }

        //Bio Section
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
        ){
            UserInfo(
                gender = user.gender,
                location = user.location,
                website = user.website
            )
            BioSection(
                userBio = user.bio
            )
        }

        //Summary section content wrapper for distance to content above
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 15.dp)
        ){
            //Summary section
            SummarySection(
                filmCount = userMovieCont, //TEMP CODE DELETE THIS
                showCount = tvShowCount, //TEMP CODE DELETE THIS
                followingCount = followingCount ,
                followersCount = followersCount,
            )
        }

        LineDevider()
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserInfo(
    gender: String,
    location: String,
    website: String
){
    FlowRow (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //Gender
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ){
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(LocalColor.current.secondary),
                    modifier = Modifier
                        .size(17.dp)
                )
                Text(
                    text = "Gender:",
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    fontSize = paragraphSize,
                    color = LocalColor.current.secondary,
                    textAlign = TextAlign.Center,
                )

            }
            Text(
                text = gender,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = paragraphSize,
                color = LocalColor.current.quinary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        top = 5.dp
                    )
            )
        }

        //Location
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(.3f)

        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ){
                Image(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(LocalColor.current.secondary),
                    modifier = Modifier
                        .size(17.dp)
                )
                Text(
                    text = "Location:",
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    fontSize = paragraphSize,
                    color = LocalColor.current.secondary
                )

            }
            Text(
                text = location,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = paragraphSize,
                color = LocalColor.current.quinary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        top = 5.dp
                    )
            )
        }


        //Website
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                    .fillMaxWidth(.3f)
            ){

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ){
                Image(
                    painter = painterResource(id = R.drawable.globe),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(LocalColor.current.secondary),
                    modifier = Modifier
                        .size(17.dp)
                )
                Text(
                    text = "Website:",
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    fontSize = paragraphSize,
                    color = LocalColor.current.secondary
                )

            }
            Text(
                text = website,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = paragraphSize,
                color = LocalColor.current.quinary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        top = 5.dp
                    )

            )
        }
    }
}

@Composable
fun BioSection (
    userBio: String,
){
    var bio = userBio

    if (bio.length < 1){
        bio = "(There doesn't seem to be anything here)"
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Bio:",
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = paragraphSize,
            color = LocalColor.current.secondary
        )
        Text(
            text = bio,
            fontFamily = fontFamily,
            fontWeight = weightLight,
            fontSize = paragraphSize,
            color = LocalColor.current.quinary,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun SummarySection (
    filmCount: Int,
    showCount: Int,
    followingCount: Int,
    followersCount: Int
){
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ){
        //Films
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = filmCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )
            Text(
                text = "Films",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )

        }

        //Shows
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = showCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )
            Text(
                text = "Shows",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )

        }

        //following
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = followingCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )
            Text(
                text = "Following",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )

        }

        //following
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = followersCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )
            Text(
                text = "Followers",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary
            )

        }
    }
}

@Composable
fun StatisticsSection(
    showGenreToPercentageMap: Map<String, Int>,
    movieGenreToPercentageMap: Map<String, Int>,
    header: String = "Statistics",
){
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = LocalConstraints.current.mainContentHorizontalPadding, end = LocalConstraints.current.mainContentHorizontalPaddingAlternative)
    ) {
        //Header
        Text(
            text = header,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = LocalColor.current.secondary,
            textAlign = TextAlign.Start
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ){
            //Show statistics

            Statistics(
                genreToPercentageMap = showGenreToPercentageMap,
                header = "Shows"
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(200.dp)
                ) {
                    LineDevider(
                        color = if(isAppInDarkTheme()) LocalColor.current.tertiary else LocalColor.current.secondaryLight,
                        strokeWith = 10f
                    )
                }
            }

            //Movie statistics
            Statistics(
                genreToPercentageMap = movieGenreToPercentageMap,
                header = "Movies"
            )
        }

    }
}

@Composable
fun Statistics(
    genreToPercentageMap: Map<String, Int>,
    header: String
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ){
        StatisticsPieChart(
            genreToPercentageMap = genreToPercentageMap,
            header = header
        )
        StatisticsList(
            genreToPercentageMap= genreToPercentageMap
        )
    }
}

@Composable
fun StatisticsPieChart (
    genreToPercentageMap: Map<String, Int>,
    header: String
){

    //Sorts the genreToPercentageMap from lowest to highest based on the values
    val sortedMap = genreToPercentageMap.toList().sortedBy { (_, value) -> value }.toMap()
    val sortedList = sortedMap.values.toList()
    val sumOfOthers = 100 - sortedList.takeLast(4).sum()
    val lastValues = sortedList.takeLast(4)
    val percentageList = listOf(sumOfOthers) + lastValues

    val colorList: Array<Color> = arrayOf(LocalColor.current.complimentaryThree, LocalColor.current.ComplimentaryFour, LocalColor.current.primary, LocalColor.current.complimentaryOne, LocalColor.current.complimentaryTwo)

    var index = 0
    var cumulativePercentage = 0f

    val pieChartRadius = 70.dp
    val pieChartStrokeWidth = 8.dp
    val animationTimeMultiplicationFactor = 7
    var animationDurationTimes = percentageList.map { it.toLong() * animationTimeMultiplicationFactor }

    val currentProgressBar = remember { mutableStateOf(0) }

    //helper effect to make the bars animate one after another
    LaunchedEffect(percentageList) {
        animationDurationTimes.forEachIndexed  { i, duration ->
            currentProgressBar.value = i
            delay(duration)
        }
    }


    Box()
    {

        //Progress bar for remaining values
        for((i, percentage) in percentageList.withIndex())
        {
            val color = if (index < colorList.size) colorList[index] else LocalColor.current.backgroundLight

            RoundProgressBar(
                percentage = if (currentProgressBar.value >= i) {1f} else {0f},
                startAngle = 360 * cumulativePercentage,
                sweepAngle = 360 * (percentage.toFloat() / 100f),
                strokeCap = StrokeCap.Butt,
                strikeWith = pieChartStrokeWidth,
                radius = pieChartRadius,
                color = color,
                animationDuration = animationDurationTimes[i].toInt(),
                easing = if(i == 0 ){
                    EaseIn} else if (i == percentageList.size -1) {
                    EaseOut} else {
                    LinearEasing}
            )
            cumulativePercentage += percentage.toFloat() / 100f

            if (index >= colorList.size -1){
                break
            }

            index++
        }

        Text(
            text = header,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = LocalColor.current.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun StatisticsList (
    genreToPercentageMap: Map<String, Int>
)
{
    //Sort genreToPercentageMap from highest to lowest based on the value
    val sortedMap = genreToPercentageMap.toList().sortedByDescending { (_, value) -> value }.toMap()
    val colorList: Array<Color> = arrayOf(LocalColor.current.complimentaryTwo, LocalColor.current.complimentaryOne, LocalColor.current.primary, LocalColor.current.ComplimentaryFour)
    var other = 100
    var index = 0

    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ){
            for(percentage in  sortedMap.values)
            {
                other -= percentage
                Text(
                    text = if(percentage >= 10) {"${percentage}%"} else {"0${percentage}%"},
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    fontSize = paragraphSize,
                    color = colorList[index],
                    textAlign = TextAlign.End,
                )
                index++
                if (index >= 4) break
            }
            index = 0
            Text(
                text = "${other}%",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = LocalColor.current.complimentaryThree,
                textAlign = TextAlign.Start,
            )
        }

        Column (
            verticalArrangement = Arrangement.spacedBy(0.dp)
        )
        {
            for(genre in  sortedMap.keys)
            {
                Text(
                    text = genre,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    fontSize = paragraphSize,
                    color = LocalColor.current.secondary,
                    textAlign = TextAlign.Start,
                )
                index++
                if (index >= 4) break
            }

            index = 0

            Text(
                text = "Other",
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                fontSize = paragraphSize,
                color = LocalColor.current.secondary,
                textAlign = TextAlign.Start,
            )


        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.5.dp)
        ) {

            for(percentage in  sortedMap.values)
            {
                ProgressBar(
                    currentNumber = percentage,
                    endNumber = 100,
                    foregroundColor = colorList[index],
                    backgroundColor = LocalColor.current.backgroundLight,
                )
                index++
                if (index >= 4) break
            }
            index = 0

            ProgressBar(
                currentNumber = other,
                endNumber = 100,
                foregroundColor = LocalColor.current.complimentaryThree,
                backgroundColor = LocalColor.current.backgroundLight,
            )

        }
    }
}


@Composable
fun TopNavigationProfileScreenLandscape(
    handleSummaryClick: () -> Unit,
    handleLibraryClick: () -> Unit,
    handleReviewsClick: () -> Unit
){

    var categoryDropDownExpanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = LocalConstraints.current.bottomUniversalNavbarHeight + 20.dp,
                end = 80.dp
            )
    ) {

        HamburgerButton(
            sizeMultiplier = 2.3f,
            handleHamburgerButtonClick = {
                categoryDropDownExpanded = !categoryDropDownExpanded
            }
        )

        Box(){
            //CategoryDropdown
            DropdownMenu(
                expanded = categoryDropDownExpanded,
                onDismissRequest = {categoryDropDownExpanded = false},
                offset = DpOffset(x = (-230).dp, y= 0.dp),
                modifier = Modifier
                    .background(color = if(isAppInDarkTheme()){ LocalColor.current.tertiary} else {LocalColor.current.primary})
                    .width(180.dp)
            ) {

                //Sorting Options
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = "Summary",
                                fontSize = headerSize,
                                fontWeight = weightBold,
                                fontFamily = fontFamily,
                                color = if(isAppInDarkTheme()){ LocalColor.current.secondary} else {LocalColor.current.backgroundLight},
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    onClick = {
                        //On click logic for dropdown menu
                        handleSummaryClick()
                        categoryDropDownExpanded = false
                    }
                )

                //Watching
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = "Library",
                                fontSize = headerSize,
                                fontWeight = weightBold,
                                fontFamily = fontFamily,
                                color = if(isAppInDarkTheme()){ LocalColor.current.secondary} else {LocalColor.current.backgroundLight},
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    onClick = {
                        //On click logic for dropdown menu
                        handleLibraryClick()
                        categoryDropDownExpanded = false
                    }
                )


                //Completed
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = "Reviews",
                                fontSize = headerSize,
                                fontWeight = weightBold,
                                fontFamily = fontFamily,
                                color = if(isAppInDarkTheme()){ LocalColor.current.secondary} else {LocalColor.current.backgroundLight},
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    onClick = {
                        //On click logic for dropdown menu
                        handleReviewsClick()
                        categoryDropDownExpanded = false
                    }
                )
            }
        }
    }

}
