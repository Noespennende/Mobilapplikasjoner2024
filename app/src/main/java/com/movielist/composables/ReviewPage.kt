package com.movielist.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.movielist.R
import com.movielist.data.ListItem
import com.movielist.data.Review
import com.movielist.data.Show
import com.movielist.data.User
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ReviewPage () {

    //TEMP CODE DELETE THIS:
    var reviewsList: List<Review> = mutableListOf()
    val emptyList: List<ListItem> = mutableListOf()

    /*
    // Populate reviewsList
    for (i in 0..10) {
        reviewsList = reviewsList + Review(
            score = Random.nextInt(0, 10),
            reviewer = User(
                userName = "User $i",
                profileImageID = R.drawable.profilepicture,
                completedShows = emptyList,
                wantToWatchShows = emptyList,
                droppedShows = emptyList,
                currentlyWatchingShows = emptyList
            ),
            likes = Random.nextInt(0, 200),
            show = Show(
                title = "Silo $i",
                length = i,
                releaseDate = Calendar.getInstance(),
                imageID = R.drawable.silo,
                imageDescription = "Silo show"
            ),
            postDate = Calendar.getInstance(),
            reviewBody = "This is a review of a show. Look how good the show is, it's very good or it might not be very good."
        )
    }

     */

    //Temp code delete the code above

    //function variables:

    var friendsReviewsList = mutableListOf(reviewsList)

    var popularReviewsThisMonthList =  mutableListOf(reviewsList)

    var popularReviewsAllTimeList = mutableListOf(reviewsList)


    //Graphics:
    TopNavBarReviewPage()

}

@Composable
fun TopNavBarReviewPage(){

    //Wrapper
    Box(
      modifier = Modifier
           .wrapContentSize()
    ){
        TopNavbarBackground()
    }
}