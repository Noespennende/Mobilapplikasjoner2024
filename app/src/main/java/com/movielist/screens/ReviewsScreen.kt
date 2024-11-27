package com.movielist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.HamburgerButton
import com.movielist.composables.LikeButton
import com.movielist.composables.LineDevider
import com.movielist.composables.LoadingCircle
import com.movielist.composables.ProfileImage
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.ProductionImage
import com.movielist.composables.TopScreensNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.ReviewsScreenTabs
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalConstraints
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.isAppInPortraitMode
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.verticalPadding
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import java.util.Calendar
import kotlin.random.Random


@Composable
fun ReviewsScreen (controllerViewModel: ControllerViewModel, navController: NavController) {

    //TEMP CODE DELETE THIS:
    var reviewsList  = mutableListOf<ReviewDTO>()

    val reviewUser = User(
        id = "IDfromFirebase",
        userName = "UserN",
        email = "user@email.com",
        followingList = mutableListOf(),
    )

    val reviewProduction = TVShow(
        imdbID = "123",
        title = "Silo",
        description = "TvShow Silo description here",
        genre = listOf("Action"),
        releaseDate = Calendar.getInstance(),
        actors = emptyList(),
        rating = 4,
        reviews = ArrayList(),
        posterUrl = "https://image.tmdb.org/t/p/w500/2asxdpNtVQhbuUJlNSQec1eprP.jpg",
        episodes = listOf("01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12"),
        seasons = listOf("1", "2", "3")
    )

    val reviewReview = Review(
        score = Random.nextInt(0, 10),
        reviewerID = reviewUser.id,
        likes = Random.nextInt(0, 200),
        productionID = reviewProduction.imdbID,
        postDate = Calendar.getInstance(),
        reviewBody = "This is a review of a show. Look how good the show is, it's very good or it might not be very good."
    )

    // Populate reviewsList
    for (i in 0..10) {
        reviewsList.add(
            ReviewDTO(
                reviewID = reviewUser.id,
                score = reviewReview.score,
                productionID = reviewReview.productionID,
                reviewerID = reviewReview.reviewerID,
                reviewBody = reviewReview.reviewBody,
                postDate = reviewReview.postDate,
                likes = reviewReview.likes,
                reviewerUserName = reviewUser.userName,
                reviewerProfileImage = reviewUser.profileImageID,
                productionPosterUrl = reviewProduction.posterUrl,
                productionTitle = reviewProduction.title,
                productionReleaseDate = reviewProduction.releaseDate,
                productionType = reviewProduction.type
            )

        )
    }

    //Temp code delete the code above

    //function variables:

    var friendsReviewsList = remember {  mutableStateOf<List<ReviewDTO>>(emptyList()) }
    val friendsReviewsListLastUpdatedTime = remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {

        val currentTime = System.currentTimeMillis()

        // Sjekker om det er første render eller har gått 2 timer siden sist oppdatering
        if (friendsReviewsList.value.isEmpty()
            || currentTime - friendsReviewsListLastUpdatedTime.longValue >= 7200000) {
            val friendsReviews = controllerViewModel.getLoggedInUsersFriendsReviews()

            friendsReviewsList.value = friendsReviews
        }
    }

    var popularReviewsThisMonthList = remember {  mutableStateOf<List<ReviewDTO>>(emptyList()) }


    LaunchedEffect(Unit) {

        val popularThisMonthList = controllerViewModel.getTop10ReviewsThisMonth()

        popularReviewsThisMonthList.value = popularThisMonthList

    }

    var popularReviewsAllTimeList = reviewsList

    val productionType by remember { mutableStateOf("Movie") }
    val productionID by remember { mutableStateOf("") }

    val review by controllerViewModel.singleReviewDTOData.collectAsState()
    val production by controllerViewModel.singleProductionData.collectAsState() /* <- Film eller TVserie objekt av filmen/serien som matcher ID i variablen over*/

    var activeTab by remember { mutableStateOf<ReviewsScreenTabs>(ReviewsScreenTabs.SUMMARY) } /*<- Active tab */

    val handleReviewLikeButtonClick: (reviewID: String) -> Unit = {reviewID ->
        //Kontroller håndtering av liking av en review her
    }

    val handleProductionClick: (productionID: String, productionType: String)
        -> Unit = {productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType))
    }
    val handleProfilePictureClick: (userID: String) -> Unit =  {userID ->
        navController.navigate(Screen.ProfileScreen.withArguments(userID))
    }

    val handleTabChange: (tab: ReviewsScreenTabs) -> Unit = {tab ->
        activeTab = tab
    }

    val handleReviewClick: (reviewID: String) -> Unit = {reviewID ->
        navController.navigate(Screen.ReviewScreen.withArguments(reviewID))
    }

    //Graphics:

    if (popularReviewsThisMonthList.value.isEmpty()){
        LoadingCircle()
    }
    else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = LocalConstraints.current.mainContentStart,
                    bottom = LocalConstraints.current.bottomUniversalNavbarHeight
                )
        )
        {
            item {
                if (activeTab == ReviewsScreenTabs.SUMMARY) {
                    SummarySection(
                        friendsReviewsList = friendsReviewsList.value,
                        topThisMonthList = popularReviewsThisMonthList.value,
                        handleReviewLikeClick = handleReviewLikeButtonClick,
                        handleProductionImageClick = handleProductionClick,
                        handleProfilePictureClick = handleProfilePictureClick,
                        handleReviewClick = handleReviewClick,
                    )
                } else if (activeTab == ReviewsScreenTabs.TOPTHISMONTH) {
                    TopThisMonthSection(
                        topThisMonthList = popularReviewsThisMonthList.value,
                        handleReviewLikeClick = handleReviewLikeButtonClick,
                        handleProductionImageClick = handleProductionClick,
                        handleProfilePictureClick = handleProfilePictureClick,
                        handleReviewClick = handleReviewClick
                    )
                } else if (activeTab == ReviewsScreenTabs.TOPALLTIME) {
                    TopAllTimeSection(
                        topAllTimeList = popularReviewsAllTimeList,
                        handleReviewLikeClick = handleReviewLikeButtonClick,
                        handleProductionImageClick = handleProductionClick,
                        handleProfilePictureClick = handleProfilePictureClick,
                        handleReviewClick = handleReviewClick
                    )
                }

            }

        }

        if (isAppInPortraitMode()){
            TopNavBarReviewPage(
                handleTabChange = handleTabChange
            )
        } else {
            topNavigationReviewsScreenLandscape(
                handleTabChange = handleTabChange
            )
        }

    }
}

@Composable
fun SummarySection (
    friendsReviewsList: List<ReviewDTO>,
    topThisMonthList: List<ReviewDTO>,
    handleReviewLikeClick: (reviewID: String) -> Unit,
    handleProductionImageClick: (productionID: String, productionType: String) -> Unit,
    handleProfilePictureClick: (userID: String) -> Unit,
    handleReviewClick: (reviewID: String) -> Unit
){
    val handleReviewLikeButtonClick: (String) -> Unit = {reviewID ->
        handleReviewLikeClick(reviewID)
    }

    Column (

    ) {
        //Latest reviews from your friends section
        ReviewsSection(
            reviewList = friendsReviewsList,
            header = "Latest reviews from the users you follow",
            handleLikeClick = handleReviewLikeButtonClick,
            handleProductionImageClick = handleProductionImageClick,
            handleProfilePictureClick = handleProfilePictureClick,
            handleReviewClick = handleReviewClick,
            paddingEnd = LocalConstraints.current.mainContentHorizontalPaddingAlternative
        )
        //Popular reviews this month section
        ReviewsSection(
            reviewList = topThisMonthList,
            header = "Popular reviews this month",
            handleLikeClick = handleReviewLikeButtonClick,
            handleProductionImageClick = handleProductionImageClick,
            handleProfilePictureClick = handleProfilePictureClick,
            handleReviewClick = handleReviewClick,
            paddingEnd = LocalConstraints.current.mainContentHorizontalPaddingAlternative
        )
    }
}

@Composable
fun TopThisMonthSection (
    topThisMonthList: List<ReviewDTO>,
    handleReviewLikeClick: (reviewID: String) -> Unit,
    handleProductionImageClick: (productionID: String, productionType: String) -> Unit,
    handleProfilePictureClick: (userID: String) -> Unit,
    handleReviewClick: (reviewID: String) -> Unit
){
    val handleReviewButtonLikeClick: (reviewID: String) -> Unit = {reviewID ->
        handleReviewLikeClick(reviewID)
    }

    //Popular reviews this month section
    ReviewsSection(
        reviewList = topThisMonthList,
        header = "Popular reviews this month",
        handleLikeClick = handleReviewButtonLikeClick,
        handleProductionImageClick = handleProductionImageClick,
        handleProfilePictureClick = handleProfilePictureClick,
        handleReviewClick = handleReviewClick
    )
}

@Composable
fun TopAllTimeSection (
    topAllTimeList: MutableList<ReviewDTO>,
    handleReviewLikeClick: (reviewID: String) -> Unit,
    handleProductionImageClick: (productionID: String, productionType: String) -> Unit,
    handleProfilePictureClick: (userID: String) -> Unit,
    handleReviewClick: (reviewID: String) -> Unit
){
    val handleReviewButtonLikeClick: (reviewID: String) -> Unit = { reviewID ->
        handleReviewLikeClick(reviewID)
    }

    //Popular reviews this month section
    ReviewsSection(
        reviewList = topAllTimeList,
        header = "Most popular reviews of all time",
        handleLikeClick = handleReviewButtonLikeClick,
        handleProductionImageClick = handleProductionImageClick,
        handleProfilePictureClick = handleProfilePictureClick,
        handleReviewClick = handleReviewClick
    )
}

@Composable
fun TopNavBarReviewPage(
    handleTabChange: (reviewsScreenTabs: ReviewsScreenTabs) -> Unit
){

    //Wrapper
    Box(
        modifier = Modifier
            .wrapContentSize()
    ){
        TopScreensNavbarBackground(
            sizeMultiplier = .8f
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(top = topNavBarContentStart)
        ) {
            ReviewCategoryOptions(handleTabChange = handleTabChange)
        }


    }
}


@Composable
fun ReviewCategoryOptions (
    activeButtonColor: Color = LocalColor.current.primary,
    inactiveButtonColor: Color = if(isAppInDarkTheme()) LocalColor.current.quaternary else LocalColor.current.primaryLight,
    handleTabChange: (reviewsScreenTabs: ReviewsScreenTabs) -> Unit
){

    var activeButton by remember {
        mutableStateOf(ReviewsScreenTabs.SUMMARY)
    }
    //Button graphics logic
    var summaryButtonColor = if (activeButton == ReviewsScreenTabs.SUMMARY) activeButtonColor else inactiveButtonColor

    var topThisMonthButtonColor =  if (activeButton == ReviewsScreenTabs.TOPTHISMONTH) activeButtonColor else inactiveButtonColor

    var topAllTimeButtonColor = if (activeButton == ReviewsScreenTabs.TOPALLTIME) activeButtonColor else inactiveButtonColor


    //Graphics
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = LocalConstraints.current.mainContentHorizontalPadding)
    ){
        item {
            //Summary button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != ReviewsScreenTabs.SUMMARY) {

                            activeButton = ReviewsScreenTabs.SUMMARY
                            handleTabChange(ReviewsScreenTabs.SUMMARY)
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
            //Top this month button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != ReviewsScreenTabs.TOPTHISMONTH) {

                            activeButton = ReviewsScreenTabs.TOPTHISMONTH
                            handleTabChange(ReviewsScreenTabs.TOPTHISMONTH)
                        }
                    }
                    .background(
                        color = topThisMonthButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Top this month",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }

        item {
            //Top all time button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != ReviewsScreenTabs.TOPALLTIME) {

                            activeButton = ReviewsScreenTabs.TOPALLTIME
                            handleTabChange(ReviewsScreenTabs.TOPALLTIME)
                        }

                    }
                    .background(
                        color = topAllTimeButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Top all time",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }
    }

}

@Composable
fun ReviewsSection(
    reviewList: List<ReviewDTO>,
    header: String,
    handleLikeClick: (reviewID: String) -> Unit,
    handleProductionImageClick: (showID: String, productionType: String) -> Unit,
    handleProfilePictureClick: (userID: String) -> Unit,
    handleReviewClick: (reviewID: String) -> Unit,
    paddingStart: Dp = LocalConstraints.current.mainContentHorizontalPadding,
    paddingEnd: Dp = LocalConstraints.current.mainContentHorizontalPadding
) {

    val handleLikeButtonClick: (String) -> Unit = {reviewID ->
        handleLikeClick(reviewID)
    }

    //Header text
    Text(
        text = header,
        fontSize = headerSize,
        fontWeight = weightBold,
        fontFamily = fontFamily,
        color = LocalColor.current.secondary,
        modifier = Modifier
            .padding(
                top = verticalPadding,
                start = paddingStart)
    )

    //Reviews container
    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingStart,
                end = paddingEnd
            )
    ) {
        LineDevider()

        if (reviewList.isNotEmpty()){
            //Reviews
            for (review in reviewList) {
                ReviewSummary(
                    review = review,
                    handleLikeClick = handleLikeButtonClick,
                    handleProductionImageClick = handleProductionImageClick,
                    handleProfilePictureClick = handleProfilePictureClick,
                    handleReviewClick = handleReviewClick
                )
                LineDevider()
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ){

                Text(
                    text = "No reviews yet",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = if(isAppInDarkTheme()) LocalColor.current.backgroundLight else LocalColor.current.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(
                            top = verticalPadding,
                            start = LocalConstraints.current.mainContentHorizontalPadding,
                            bottom = 20.dp)
                )
            }

        }


    }
}

@Composable
fun ReviewSummary (
    review: ReviewDTO,
    handleLikeClick: (String) -> Unit,
    handleProductionImageClick: (showID: String, productionType: String) -> Unit,
    handleProfilePictureClick: (userID: String) -> Unit,
    handleReviewClick: (reviewID: String) -> Unit
) {
    val handleLikeButtonClick: () -> Unit = {
        handleLikeClick(review.reviewID.toString())
    }

    //Main container
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ProductionImage(
                imageID = review.productionPosterUrl,
                modifier = Modifier
                    .clickable {
                        handleProductionImageClick(review.productionID, review.productionType)
                    }
            )
            //Review header, score and body
            Column (
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .padding(
                        start = 5.dp
                    )
            ) {
                Row (
                ){
                    //Review Header and user section
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    {
                        //Review header and score
                        Column (
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                        )
                        {
                            //Header
                            Text(
                                text = "${review.productionTitle} (${review.productionReleaseDate.get(Calendar.YEAR)})",
                                fontSize = paragraphSize,
                                fontFamily = fontFamily,
                                fontWeight = weightBold,
                                color = LocalColor.current.secondary
                            )
                        }

                        //Userinfo and review date
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clickable {
                                    handleProfilePictureClick(review.reviewerID)
                                }
                        ){
                            //Username and review
                            Column (
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier
                                    .fillMaxWidth(.6f)
                            ){
                                //Username
                                Text(
                                    text = review.reviewerUserName,
                                    fontSize = paragraphSize,
                                    fontFamily = fontFamily,
                                    fontWeight = weightBold,
                                    textAlign = TextAlign.End,
                                    color = LocalColor.current.secondary
                                )
                            }
                            //profile picture
                            ProfileImage(
                                imageID = review.reviewerProfileImage,
                                userName = review.reviewerUserName,
                                handleProfileImageClick = {
                                    handleProfilePictureClick(review.reviewerID)
                                }
                            )
                        }
                    }
                }

                //Review date and score
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    //Score
                    RatingsGraphics(
                        review.score,
                    )
                }

                //Body
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                {

                    Text(
                        text = TruncateReviewSummaryText(review.reviewBody),
                        fontSize = paragraphSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = LocalColor.current.quinary,
                    )

                    Text(
                        text = "(Click to see full review)",
                        fontSize = paragraphSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = LocalColor.current.primary,
                        modifier = Modifier
                            .fillMaxWidth(.8f)
                            .clickable {
                                handleReviewClick(review.reviewID)
                            }
                    )
                }
            }
        }
        Box(
            Modifier.fillMaxWidth()
                .padding(
                    top = 5.dp
                )
        ){
            //review date
            Text(
                text = "Posted: ${review.postDate.get(Calendar.DATE)}/${review.postDate.get(Calendar.MONTH)+1}/${review.postDate.get(Calendar.YEAR)}",
                fontSize = paragraphSize,
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                color = LocalColor.current.quinary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
            )
            Text(
                text = "${review.likes} likes",
                fontSize = paragraphSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = LocalColor.current.secondary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }
        LineDevider()


        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            LikeButton(
                handleLikeClick = handleLikeButtonClick
            )
        }
    }

}

@Composable
fun topNavigationReviewsScreenLandscape(
    handleTabChange: (ReviewsScreenTabs) -> Unit,
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
                        handleTabChange(ReviewsScreenTabs.SUMMARY)
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
                                text = "Top this month",
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
                        handleTabChange(ReviewsScreenTabs.TOPTHISMONTH)
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
                                text = "Top all time",
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
                        handleTabChange(ReviewsScreenTabs.TOPALLTIME)
                        categoryDropDownExpanded = false
                    }
                )
            }


        }
    }

}

fun TruncateReviewSummaryText (reviewBody: String): String {
    val words = reviewBody.split(" ")
    var truncatedReviewBody = ""

    if (words.size > 15) {
        truncatedReviewBody = words.take(20).joinToString(" ") + "..."
    } else {
        truncatedReviewBody = reviewBody
    }

    return truncatedReviewBody
}

