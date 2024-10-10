package com.movielist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.movielist.R
import com.movielist.data.ListItem
import com.movielist.data.Review
import com.movielist.data.Show
import com.movielist.data.ShowSortOptions
import com.movielist.data.User
import com.movielist.data.ReviewOptions
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkPurple
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
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import java.util.Calendar
import kotlin.random.Random


@Composable
fun ReviewPage () {

    //TEMP CODE DELETE THIS:
    var reviewsList  = mutableListOf<Review>()
    val emptyList = mutableListOf<ListItem>()

    // Populate reviewsList
    for (i in 0..10) {
        reviewsList.add(Review(
            score = Random.nextInt(0, 10),
            reviewer = User(
                userName = "User $i",
                profileImageID = R.drawable.profilepicture,
                completedShows = emptyList,
                wantToWatchShows = emptyList,
                droppedShows = emptyList,
                currentlyWatchingShows = emptyList,
                email = "user@email.com",
            ),
            likes = Random.nextInt(0, 200),
            show = TVShow(
                imdbID = "123",
                title = "Silo",
                description = "TvShow Silo description here",
                genre = "Action",
                releaseDate = Calendar.getInstance(),
                actors = emptyList(),
                rating = 4,
                reviews = ArrayList(),
                posterUrl = R.drawable.silo,
                episodes = listOf("01", "02", "03", "04", "05", "06",
                                 "07", "08", "09", "10", "11", "12"),
                seasons = listOf("1", "2", "3")
            ),
            postDate = Calendar.getInstance(),
            reviewBody = "This is a review of a show. Look how good the show is, it's very good or it might not be very good."
        ))
    }

    //Temp code delete the code above

    //function variables:
    var friendsReviewsList = reviewsList
    var popularReviewsThisMonthList =  reviewsList
    var popularReviewsAllTimeList = reviewsList

    //Graphics:
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topNavBaHeight + 40.dp)
    )
    {
        item {
            SummarySection(
                friendsReviewsList = friendsReviewsList,
                topThisMonthList = popularReviewsThisMonthList
            )
        }

    }
    TopNavBarReviewPage()
}

@Composable
fun SummarySection (
    friendsReviewsList: MutableList<Review>,
    topThisMonthList: MutableList<Review>
){
    Column () {
        //Latest reviews from your friends section
        ReviewsSection(
            reviewList = friendsReviewsList,
            header = "Latest reviews from your friends"
        )
        //Popular reviews this month section
        ReviewsSection(
            reviewList = topThisMonthList,
            header = "Popular reviews this month"
        )
    }
}

@Composable
fun TopThisMonthSection (
    topThisMonthList: MutableList<Review>
){
    //Popular reviews this month section
    ReviewsSection(
        reviewList = topThisMonthList,
        header = "Popular reviews this month"
    )
}

@Composable
fun TopAllTimeSection (
    topAllTimeList: MutableList<Review>
){
    //Popular reviews this month section
    ReviewsSection(
        reviewList = topAllTimeList,
        header = "Most popular reviews of all time"
    )
}

@Composable
fun TopNavBarReviewPage(){

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
            CategorySelectButton()
            ReviewCategoryOptions()
        }


    }
}


@Composable
fun ReviewCategoryOptions (
    activeButtonColor: Color = Purple,
    inactiveButtonColor: Color = LightGray,
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
        mutableStateOf(ReviewOptions.SUMMARY)
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
                        if (activeButton != ReviewOptions.SUMMARY) {

                            activeButton = ReviewOptions.SUMMARY
                            summaryButtonColor = activeButtonColor
                            topThisMonthButtonColor = inactiveButtonColor
                            topAllTimeButtonColor = inactiveButtonColor
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
                        if (activeButton != ReviewOptions.TOPTHISMONTH) {

                            activeButton = ReviewOptions.TOPTHISMONTH
                            summaryButtonColor = inactiveButtonColor
                            topThisMonthButtonColor = activeButtonColor
                            topAllTimeButtonColor = inactiveButtonColor
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
                        if (activeButton != ReviewOptions.TOPALLTIME) {

                            activeButton = ReviewOptions.TOPALLTIME
                            summaryButtonColor = inactiveButtonColor
                            topThisMonthButtonColor = inactiveButtonColor
                            topAllTimeButtonColor = activeButtonColor
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
fun CategorySelectButton () {
    //Category button

    var dropDownExpanded by remember {
        mutableStateOf(false)
    }
    var dropDownButtonText by remember{
        mutableStateOf("Movies & Shows")
    }
    val sortOptions = listOf(
        ShowSortOptions.MOVIESANDSHOWS,ShowSortOptions.MOVIES, ShowSortOptions.SHOWS
    )


    //Wrapper
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //CategorySelectButton
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .width(200.dp)
                .align(Alignment.Center)
                .clickable {
                    //dropdown menu button logic
                    dropDownExpanded = true
                }
        ){
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
                    color = Purple,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "V",
                    fontSize = paragraphSize,
                    fontWeight = weightLight,
                    fontFamily = fontFamily,
                    color = Purple,
                )

            }

            //MENU
            DropdownMenu(
                expanded = dropDownExpanded,
                onDismissRequest = {dropDownExpanded = false},
                offset = DpOffset(x = 50.dp, y= 0.dp),
                modifier = Modifier
                    .background(color = DarkPurple)
                    .width(100.dp)
            ) {
                sortOptions.forEach{
                        option -> DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = GenerateShowSortOptionName(option),
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
                        dropDownButtonText = GenerateShowSortOptionName(option)
                    })
                }
            }

        }
   }
}

@Composable
fun ReviewsSection(
    reviewList: List<Review>,
    header: String
) {

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
        //Reviews
        for (review in reviewList) {
            ReviewSummary(
                review = review
            )
            LineDevider()
        }

    }
}

@Composable
fun ReviewSummary (
    review: Review
) {
    //Main container
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ShowImage(
            imageID = review.show.posterUrl
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
                    )
                    {
                        //Header
                        Text(
                            text = "${review.show.title} (${review.show.releaseDate.get(Calendar.YEAR)})",
                            fontSize = paragraphSize,
                            fontFamily = fontFamily,
                            fontWeight = weightBold,
                            color = White
                        )
                        //Score
                        ScoreGraphics(
                            review.score
                        )
                    }

                    //Userinfo and review date
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ){
                        //Username and review date
                        Column (
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            horizontalAlignment = Alignment.End
                        ){
                            //Username
                            Text(
                                text = review.reviewer.userName,
                                fontSize = paragraphSize,
                                fontFamily = fontFamily,
                                fontWeight = weightBold,
                                color = White
                            )
                            //review date
                            Text(
                                text = "${review.postDate.get(Calendar.DATE)}/${review.postDate.get(Calendar.MONTH)}/${review.postDate.get(Calendar.YEAR)}",
                                fontSize = paragraphSize,
                                fontFamily = fontFamily,
                                fontWeight = weightRegular,
                                color = darkWhite
                            )
                        }
                        //profile picture
                        ProfileImage(
                            imageID = review.reviewer.profileImageID,
                            userName = review.reviewer.userName
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
                Text(
                    text = review.reviewBody,
                    fontSize = paragraphSize,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    color = darkWhite,
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .height(60.dp)
                )

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
                LikeButton()
            }



        }
    }
}
