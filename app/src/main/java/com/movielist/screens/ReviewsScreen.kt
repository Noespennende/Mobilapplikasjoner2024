package com.movielist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.LikeButton
import com.movielist.composables.LineDevider
import com.movielist.composables.ProfileImage
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.ProductionImage
import com.movielist.composables.ProductionSortSelectButton
import com.movielist.composables.TopNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.ReviewsScreenTabs
import com.movielist.model.ShowSortOptions
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.darkWhite
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBaHeight
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
        friendList = mutableListOf(),
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

    var activeSortOption by remember { mutableStateOf<ShowSortOptions>(ShowSortOptions.MOVIESANDSHOWS) } /*<- Current sort option*/
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

    val handleSortChange: (sortOption: ShowSortOptions) -> Unit = {sortOption ->
        activeSortOption = sortOption
        //Kontrollerfunksjon for å håndtere sortering her
    }

    val handleTabChange: (tab: ReviewsScreenTabs) -> Unit = {tab ->
        activeTab = tab
    }

    val handleReviewClick: (reviewID: String) -> Unit = {reviewID ->
        navController.navigate(Screen.ReviewScreen.withArguments(reviewID))
    }

    //Graphics:
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topNavBaHeight + 40.dp)
    )
    {
        item {
            if(activeTab == ReviewsScreenTabs.SUMMARY){
                SummarySection(
                    friendsReviewsList = friendsReviewsList.value,
                    topThisMonthList = popularReviewsThisMonthList.value,
                    handleReviewLikeClick = handleReviewLikeButtonClick,
                    handleProductionImageClick = handleProductionClick,
                    handleProfilePictureClick = handleProfilePictureClick,
                    handleReviewClick = handleReviewClick
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
                TopAllTimeSection (
                    topAllTimeList = popularReviewsAllTimeList,
                    handleReviewLikeClick = handleReviewLikeButtonClick,
                    handleProductionImageClick = handleProductionClick,
                    handleProfilePictureClick = handleProfilePictureClick,
                    handleReviewClick = handleReviewClick
                )
            }

        }

    }
    TopNavBarReviewPage(
        handleSortChange = handleSortChange,
        handleTabChange = handleTabChange
    )
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

    Column () {
        //Latest reviews from your friends section
        ReviewsSection(
            reviewList = friendsReviewsList,
            header = "Latest reviews from your friends",
            handleLikeClick = handleReviewLikeButtonClick,
            handleProductionImageClick = handleProductionImageClick,
            handleProfilePictureClick = handleProfilePictureClick,
            handleReviewClick = handleReviewClick
        )
        //Popular reviews this month section
        ReviewsSection(
            reviewList = topThisMonthList,
            header = "Popular reviews this month",
            handleLikeClick = handleReviewLikeButtonClick,
            handleProductionImageClick = handleProductionImageClick,
            handleProfilePictureClick = handleProfilePictureClick,
            handleReviewClick = handleReviewClick
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
    handleSortChange: (activeCategory: ShowSortOptions) -> Unit,
    handleTabChange: (reviewsScreenTabs: ReviewsScreenTabs) -> Unit
){

    //Wrapper
    Box(
        modifier = Modifier
            .wrapContentSize()
    ){
        TopNavbarBackground()
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(top = topNavBarContentStart)
        ) {
            ProductionSortSelectButton(
                handleSortChange = handleSortChange
            )
            ReviewTabOptions(handleTabChange = handleTabChange)
        }


    }
}


@Composable
fun ReviewTabOptions (
    activeButtonColor: Color = Purple,
    inactiveButtonColor: Color = LightGray,
    handleTabChange: (reviewsScreenTabs: ReviewsScreenTabs) -> Unit
){

    //Button graphics logic
    var summaryButtonColor by remember {
        mutableStateOf(activeButtonColor)
    }
    var topThisMonthButtonColor by remember {
        mutableStateOf(inactiveButtonColor)
    }
    var topAllTimeButtonColor by remember {
        mutableStateOf(inactiveButtonColor)
    }
    var activeButton by remember {
        mutableStateOf(ReviewsScreenTabs.SUMMARY)
    }

    //Graphics
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = horizontalPadding)
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
                            summaryButtonColor = activeButtonColor
                            topThisMonthButtonColor = inactiveButtonColor
                            topAllTimeButtonColor = inactiveButtonColor
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
                    color = DarkGray
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
                            summaryButtonColor = inactiveButtonColor
                            topThisMonthButtonColor = activeButtonColor
                            topAllTimeButtonColor = inactiveButtonColor
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
                    color = DarkGray
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
                            summaryButtonColor = inactiveButtonColor
                            topThisMonthButtonColor = inactiveButtonColor
                            topAllTimeButtonColor = activeButtonColor
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
                    color = DarkGray
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
    handleReviewClick: (reviewID: String) -> Unit
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
        color = White,
        modifier = Modifier
            .padding(
                top = verticalPadding,
                start = horizontalPadding)
    )

    //Reviews container
    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = horizontalPadding,
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
                    color = LightGray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(
                            top = verticalPadding,
                            start = horizontalPadding)
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
                .clickable {
                    handleReviewClick(review.reviewID)
                }
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
                    )
                    {
                        //Header
                        Text(
                            text = "${review.productionTitle} (${review.productionReleaseDate.get(Calendar.YEAR)})",
                            fontSize = paragraphSize,
                            fontFamily = fontFamily,
                            fontWeight = weightBold,
                            color = White
                        )
                        //Score
                        RatingsGraphics(
                            review.score,
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
                        //Username and review date
                        Column (
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            horizontalAlignment = Alignment.End
                        ){
                            //Username
                            Text(
                                text = review.reviewerUserName,
                                fontSize = paragraphSize,
                                fontFamily = fontFamily,
                                fontWeight = weightBold,
                                color = White
                            )
                            //review date
                            Text(
                                text = "${review.postDate.get(Calendar.DATE)}/${review.postDate.get(Calendar.MONTH)+1}/${review.postDate.get(Calendar.YEAR)}",
                                fontSize = paragraphSize,
                                fontFamily = fontFamily,
                                fontWeight = weightRegular,
                                color = darkWhite
                            )
                        }
                        //profile picture
                        ProfileImage(
                            imageID = review.reviewerProfileImage,
                            userName = review.reviewerUserName
                        )
                    }

                }
            }

            //Body
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                ) {
                    Text(
                        text = TruncateReviewSummaryText(review.reviewBody),
                        fontSize = paragraphSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = darkWhite,
                    )
                    if (ReviewBodyIsTruncated(review.reviewBody)){
                        Text(
                            text = "(Click to read more)",
                            fontSize = paragraphSize,
                            fontFamily = fontFamily,
                            fontWeight = weightRegular,
                            color = Purple,
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                        )
                    }
                }


                Text(
                    text = "${review.likes} likes",
                    fontSize = paragraphSize,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = White,
                    modifier = Modifier
                        .align(Alignment.Bottom)
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

fun ReviewBodyIsTruncated (reviewBody: String): Boolean {
    val words = reviewBody.split(" ")
    if (words.size > 15) {
        return true
    }
    return false
}