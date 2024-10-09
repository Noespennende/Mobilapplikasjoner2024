package com.movielist.composables

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movielist.R
import com.movielist.data.ListItem
import com.movielist.data.ProfileCategoryOptions
import com.movielist.data.Review
import com.movielist.data.TVShow
import com.movielist.data.User
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.weightBold
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ProfilePage (){

    // TEMP CODE DELETE BELOW
    val exampleUser: User = User(
        userName = "Example User",
        email = "Example@Email.com",
        profileImageID = R.drawable.profilepicture
    )

    val exampleReviews: MutableList<Review> = mutableListOf()
    val exampleShows: MutableList<ListItem> = mutableListOf()

    for (i in 0 .. 10){
        exampleShows.add(
            ListItem(
                production = TVShow(
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
                currentEpisode = i,
                score = Random.nextInt(0, 10)

            )
        )
        exampleReviews.add(
            Review(
                score = Random.nextInt(0, 10),
                reviewer = exampleUser,
                show = exampleShows[i].production,
                reviewBody = "This is a review of the show",
            )
        )
    }

    exampleUser.myReviews.addAll(exampleReviews)
    exampleUser.currentlyWatchingShows.addAll(exampleShows)
    exampleUser.completedShows.addAll(exampleShows)
    exampleUser.wantToWatchShows.addAll(exampleShows)
    exampleUser.droppedShows.addAll(exampleShows)

    // TEMP CODE DELETE ABOVE

    //function variables:
    val user by remember {
        mutableStateOf(exampleUser)
    }
    val loggedInUser by remember {
        mutableStateOf(true)
    }

    //Graphics
    TopNavBarProfilePage(
        user = exampleUser,
        loggedInUser = loggedInUser
    )


}

@Composable
fun TopNavBarProfilePage(
    user: User,
    loggedInUser: Boolean
) {
    TopNavbarBackground()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        UsernameHeadline(
            user = user,
            loggedInUser = loggedInUser
        )

        ProfileCategoryOptions()

    }

}

@Composable
fun UsernameHeadline (
    user: User,
    loggedInUser: Boolean
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topNavBarContentStart)
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
            color = if(loggedInUser){Purple} else {LightGray},
            modifier = Modifier
                .padding(horizontal = 10.dp)
        )
    }
}

@Composable
fun ProfileCategoryOptions(
    activeButtonColor: Color = Purple,
    inactiveButtonColor: Color = LightGray,
){

        //Button graphics logic
        var summaryButtonColor by remember {
            mutableStateOf(activeButtonColor)
        }
        var libaryButtonColor by remember {
            mutableStateOf(inactiveButtonColor)
        }
        var reviewsButtonColor by remember {
            mutableStateOf(inactiveButtonColor)
        }

        var activeButton by remember {
            mutableStateOf(ProfileCategoryOptions.SUMMARY)
        }

        //Graphics
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding)
        ){
            item {
                //Summary
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable {
                            //OnClickFunction
                            if (activeButton != ProfileCategoryOptions.SUMMARY) {

                                activeButton = ProfileCategoryOptions.SUMMARY
                                summaryButtonColor = activeButtonColor
                                libaryButtonColor = inactiveButtonColor
                                reviewsButtonColor = inactiveButtonColor
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
                //Completed
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable {
                            //OnClickFunction
                            if (activeButton != ProfileCategoryOptions.LIBRARY) {

                                activeButton = ProfileCategoryOptions.LIBRARY
                                summaryButtonColor = inactiveButtonColor
                                libaryButtonColor = activeButtonColor
                                reviewsButtonColor = inactiveButtonColor
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
                        color = DarkGray
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
                            if (activeButton != ProfileCategoryOptions.REVIEWS) {

                                activeButton = ProfileCategoryOptions.REVIEWS
                                summaryButtonColor = inactiveButtonColor
                                libaryButtonColor = inactiveButtonColor
                                reviewsButtonColor = activeButtonColor
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
                        color = DarkGray
                    )
                }
            }
        }
}
