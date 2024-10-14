package com.movielist.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.movielist.R
import com.movielist.data.Episode
import com.movielist.data.ListItem
import com.movielist.data.Movie
import com.movielist.data.Production
import com.movielist.data.Review
import com.movielist.data.Show
import com.movielist.data.TVShow
import com.movielist.data.User
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.White
import com.movielist.ui.theme.*
import java.io.Console
import java.util.Calendar
import kotlin.random.Random

@Composable
fun FrontPage() {

    //Temporary code: DELETE THIS CODE
    val listItemList = mutableListOf<ListItem>()
    for (i in 0..12) {
        listItemList.add(
            ListItem(
                currentEpisode = i,
                score = Random.nextInt(0, 10),
                production = TVShow(
                    imdbID = "123",
                    title = "Silo",
                    description = "TvShow Silo description here",
                    genre = listOf("Action"),
                    releaseDate = Calendar.getInstance(),
                    actors = emptyList(),
                    rating = 4,
                    reviews = ArrayList(),
                    posterUrl = R.drawable.silo,
                    episodes = listOf("01", "02", "03", "04", "05", "06",
                                      "07", "08", "09", "10", "11", "12"),
                    seasons = listOf("1", "2", "3")
                ),
            )
        )
    }

    val showList = mutableListOf<Production>()

    for (i in 0..12) {
        showList.add(
            TVShow(
                imdbID = "123",
                title = "Silo",
                description = "TvShow Silo description here",
                genre = listOf("Action"),
                releaseDate = Calendar.getInstance(),
                actors = emptyList(),
                rating = 4,
                reviews = ArrayList(),
                posterUrl = R.drawable.silo,
                episodes = listOf("01", "02", "03", "04", "05", "06",
                    "07", "08", "09", "10", "11", "12"),
                seasons = listOf("1", "2", "3")
            )
        )
    }

    val reviewList = mutableListOf<Review>()
    val user = User(
        id = "testid",
        userName = "User Userson",
        email = "test@email.no",
        friendList = mutableListOf(),
        myReviews = mutableListOf(),
        favoriteCollection = mutableListOf(),
        profileImageID = R.drawable.profilepicture,
        completedShows = listItemList,
        wantToWatchShows = listItemList,
        droppedShows = listItemList,
        currentlyWatchingShows = listItemList
    )
    for (i in 0..6) {
        reviewList.add(
            Review(
                score = Random.nextInt(0, 10), //<- TEMP CODE: PUT IN REAL CODE
                reviewer = user,
                show = listItemList[1].production,
                reviewBody = "It’s reasonably well-made, and visually compelling," +
                        "but it’s ultimately too derivative, and obvious in its thematic execution," +
                        "to recommend..",
                postDate = Calendar.getInstance(),
                likes = Random.nextInt(0, 100) //<- TEMP CODE: PUT IN REAL CODE
            )
        )
    }
    //^^^KODEN OVENFOR ER MIDLERTIDIG. SLETT DEN.^^^^

    //Front page graphics
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Front page content
        item {
            CurrentlyWatchingScroller(listItemList)
        }

        item {
            // Funksjon som returnerer de 10 mest populære filmene og seriene i appen.
            // Funksjonen returnerer en liste med Show objekter.

            // Gjorde om slik at funksjonen tar imot Production objekter istedenfor Show objekter
            // Manglet viss data i Show data klassen og ville ikke styre for mye rundt, kan endre hva som tas inn senere
            var allShowsList = mutableListOf<Production>()

            // Kan slenge alt inn i variebelet ovenfor, mest for logisk navngivning atm.
            var top10Shows = allShowsList
                .sortedByDescending { it.rating }
                .take(10) // Henter kun de første 10

            ProductionListSidesroller(
                header = "Popular shows and movies",
                listOfShows = top10Shows,
                textModifier = Modifier
                    .padding(vertical = 10.dp, horizontal = horizontalPadding),
                contentModifier = Modifier
                    .padding(top = verticalPadding)
            )
        }

        item {
            YourFriendsJustWatched(listItemList)
        }

        item {
            // Funksjon som returnerer de 10 reviewene som har fått flest likes den siste uken.
            // Funksjonen returnerer en liste med Review objekter som  er sortert fra flest til ferrest likes.

            var reviewsList  = mutableListOf<Review>()
            var reviewsListPastWeek = mutableListOf<Review>()
            val currentDate = Calendar.getInstance()
            val pastWeek = currentDate.apply {
                add(Calendar.DATE, -7)
            }

            for (review in reviewsList) {
                if (review.postDate >= pastWeek) {
                    reviewsListPastWeek.add(review)
                } else {
                    print("Review not posted within the past 7 days")
                }
            }

            var top10ReviewsListPastWeek = reviewsListPastWeek
                .sortedByDescending { it.score }
                .take(10)

            //Top reviews this week:
            ReviewsSection(
                reviewList = top10ReviewsListPastWeek,
                header = "Top reviews this week"
            )
        }

        item {
            /*Adds empty space the size of the bottom nav bar to ensure content don't dissapear
            behind it*/
            Spacer(modifier = Modifier.height(bottomNavBarHeight))
        }

    }

}

@Composable
fun CurrentlyWatchingScroller (
    listOfShows: List<ListItem>
) {
// Funksjon som returnerer alle filmer og serier som ligger i den LOGGED INN brukeren sin Currently Watching liste.
// Funksjonen returnerer en liste med ListItem objekter og er sortert i henhold til hvilke som var sist oppdatert

    var allCurrentlyWatchingShows = mutableListOf<ListItem>()

    val testUser = remember {mutableStateOf<User?>(null)} // Usikker på om det er riktig bruker som skal hentes

    // Sjekker om bruker har en currentlyWatchingShows liste
    testUser.value?.currentlyWatchingShows?.let { shows ->
        allCurrentlyWatchingShows.addAll(shows)
    }

    // Holder på oversikten over nyligste klikk
    var clickTimes by remember {mutableStateOf(mutableMapOf<String, Long>())}

    fun mostRecentButtonClick(show: ListItem) {
        clickTimes[show.id] = System.currentTimeMillis() // Registerer når currentEpisode på watchedEpisodesCount oppdateres (knapp trykkes)

        allCurrentlyWatchingShows = listOfShows.sortedByDescending {clickTimes[it.id]}.toMutableList()
    }
        LazyRow (
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(start = horizontalPadding, end = 0.dp)
    ) {
        items(listOfShows.size) { i ->
            CurrentlyWatchingCard(
                imageId = listOfShows[i].production.posterUrl,
                imageDescription = listOfShows[i].production.title + " Image description",
                title = listOfShows[i].production.title,
                showLenght = when (listOfShows[i].production) {
                    is TVShow -> (listOfShows[i].production as TVShow).episodes.size // Returnerer antall episoder som Int
                    is Movie -> (listOfShows[i].production as Movie).lengthMinutes // Returnerer lengden i minutter som Int
                    is Episode -> (listOfShows[i].production as Episode).lengthMinutes
                    else -> 0 // En fallback-verdi hvis det ikke er en TvShow, Movie eller Episode
                },
                episodesWatched = listOfShows[i].currentEpisode,
                onMarkAsWatched = {mostRecentButtonClick(listOfShows[i])} // Registrerer når "Mark as Watched" er trykket

            )
        }


    }
}

@Composable
fun CurrentlyWatchingCard (
    imageId: Int = R.drawable.noimage,
    imageDescription: String = "Image not available",
    title: String,
    showLenght: Int?,
    episodesWatched: Int,
    modifier: Modifier = Modifier,
    onMarkAsWatched: () -> Unit
    ) {

    var watchedEpisodesCount: Int by remember {
        mutableIntStateOf(episodesWatched)
    }

    var buttonText by remember {
        mutableStateOf(generateButtonText(episodesWatched, showLenght))
    }

    //Card container
    Card (
        modifier = modifier
            .width(350.dp),
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Gray)

    ){
        //card content
        Column(modifier = Modifier
            .height(265.dp+ topPhoneIconsBackgroundHeight)
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = (topPhoneIconsBackgroundHeight+10.dp),
                bottom = 10.dp))
        {
            //Main image
            Image(
                painter = painterResource(id = imageId),
                contentDescription = imageDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(5.dp)))

            //Content under image
            Column(modifier = Modifier
                .fillMaxSize()
                )
            {
                //Title and episodes watched
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    //Title
                    Text(
                        title,
                        style = TextStyle(
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = weightRegular
                            )
                    )
                    //Episodes watched
                    Text (
                        "Ep $watchedEpisodesCount of $showLenght",
                        style = TextStyle(
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = weightLight
                        )
                    )
                }

                //Progress bar
                ProgressBar(currentNumber = watchedEpisodesCount, endNumber = showLenght!!)

                //Mark as watched button
                Button(
                    onClick = {
                        //Button onclick function
                        if ( watchedEpisodesCount < showLenght) {
                            watchedEpisodesCount++
                        }

                        buttonText = generateButtonText(watchedEpisodesCount, showLenght)

                        onMarkAsWatched()

                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Purple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                ) {
                    //Button text
                    Text(
                        buttonText,
                        fontSize = headerSize,
                        fontWeight = weightRegular,
                        color = DarkGray
                    )
                }
            }

        }
    }
}


@Composable
fun YourFriendsJustWatched (
    listOfShows: List<ListItem>
) {
    // Funksjon som retunerer de 10 siste showene som har blitt oppdatert blant vennene til brukeren som er logget inn.
    // Funksjonen returnerer en liste med ListItem objekter der hvert list item er hentet fra listen til vennen som list itemet er relevant for.

    // Funker nok ikke 100% når det gjelder nyligste klikk, må tilbake å teste ordentlig
    var friendsList = mutableListOf<User>()

    var allFriendsCurrentlyWatchingShows = mutableListOf<Pair<ListItem, User>>()

    // Holder på oversikten over nyligste klikk, teste om klikk registreres ordentlig senere
    var clickTimes by remember { mutableStateOf(mutableMapOf<String, Long>()) }

    // Henter alle shows fra currentlyWatchingShows listen til venner
    friendsList.forEach { friend ->
        friend.currentlyWatchingShows?.let { shows ->
            shows.forEach { show ->
                allFriendsCurrentlyWatchingShows.add(show to friend)
            }
        }
    }

    // Sorterer showene venner ser på ut ifra nyligste klikk, nyligste klikk øverst
    val sortedShows = allFriendsCurrentlyWatchingShows
        .sortedByDescending { (show) ->
            clickTimes[show.id]
        }
        .take(10)


    //Container collumn
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = verticalPadding)
    ) {
        //Header
        Text(
            "Your friends just watched",
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightBold,
            color = White,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = horizontalPadding)
        )
        //Content
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = horizontalPadding, end = 0.dp)
        ){
            items (sortedShows.size) {i ->
                //Info for each show
                Column (
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    ShowImage(
                        imageID = sortedShows[i].first.production.posterUrl,
                        imageDescription = sortedShows[i].first.production.title + " Poster"
                    )
                    //Friend Info
                    FriendsWatchedInfo(
                        profileImageID = R.drawable.profilepicture,
                        profileName = "User Userson", //TEMP DELETE THIS
                        episodesWatched = i,
                        showLenght = when (sortedShows[i].first.production) {
                            is TVShow -> (sortedShows[i].first.production as TVShow).episodes.size // Returnerer antall episoder som Int
                            is Movie -> (sortedShows[i].first.production as Movie).lengthMinutes // Returnerer lengden i minutter som Int
                            is Episode -> (sortedShows[i].first.production as Episode).lengthMinutes
                            else -> 0 // En fallback-verdi hvis det ikke er en TvShow, Movie eller Episode
                        },
                        score = sortedShows[i].first.score
                    )
                }


            }
        }

    }
}

@Composable
fun FriendsWatchedInfo(
    profileImageID: Int,
    profileName: String,
    episodesWatched: Int,
    showLenght: Int?,
    score: Int = 0,

) {
    Row(
        horizontalArrangement =  Arrangement.spacedBy(3.dp)
    ) {
        ProfileImage(
            imageID = profileImageID,
            userName = profileName
        )
        //Episode Count and Score
        Column (
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ){
            Text(
                text = "Ep $episodesWatched of $showLenght",
                color = White,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = 12.sp
            )
            ScoreGraphics(
                score = score
            )
        }
    }

}

//Utility Functions
fun generateButtonText(
    episodesWatched: Int,
    showLenght: Int?)
: String
{
    if (episodesWatched+1 == showLenght) {
        return "Mark as completed"
    } else if ( episodesWatched == showLenght){
        return "Add a rating"
    }
    else {
        return "Mark episode ${episodesWatched + 1} as watched"
    }

}