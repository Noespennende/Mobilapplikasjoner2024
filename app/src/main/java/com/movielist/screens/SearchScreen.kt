package com.movielist.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.R
import com.movielist.Screen
import com.movielist.composables.ProductionImage
import com.movielist.composables.ProfileImage
import com.movielist.composables.TopNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.Production
import com.movielist.model.SearchSortOptions
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.*
import java.util.Calendar


@Composable
fun SearchPage (controllerViewModel: ControllerViewModel, navController: NavController) {
    //TEMP CODE DELETE THIS

    val tempShowList = mutableListOf<Production>()
    val tempUserList = mutableListOf<User>()

    for (i in 0..50) {
        tempShowList.add(
            TVShow(
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
            ),
        )

        tempUserList.add(
            User(
                email = "lol@email.com",
                userName = "Jane User",
                profileImageID = R.drawable.profilepicture.toString()
            )
        )
    }
    //TEMP CODE DELETE ABOVE
    val searchResultsList by controllerViewModel.searchResults.collectAsState()
    //val productionList: MutableList<Production> = tempShowList /*<-- Liste som inneholder søkeresultatene for Movies og TVSerier*/
    val userList: MutableList<User> = tempUserList /* <-- Liste som inneholder søkeresultatene for brukere */

    var activeSortOption by remember { mutableStateOf(SearchSortOptions.MOVIESANDSHOWS) }

    val handleSearchQuery: (sortingOption: SearchSortOptions, searchQuery:String) -> Unit = {sortingOption, searchQuery ->
        activeSortOption = sortingOption
        //Kontroller logikk for å håndtere søk her
        controllerViewModel.searchMultibleMedia(searchQuery)
    }




    val handleSortOptionsChange: (sortingOption: SearchSortOptions) -> Unit = {sortingOption ->
        activeSortOption = sortingOption
        //kontroller logikk for å håndtere sortering her

    }

    val handleUserClick: (userID: String) -> Unit = {userID ->
        navController.navigate(Screen.ProfileScreen.withArguments(userID))
    }

    val handleProductionClick: (productionID: String, productionType: String) -> Unit = {productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType))
    }


    //Graphics:
    //Search result content
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = showImageWith),
        contentPadding = PaddingValues(
            top = topNavBaHeight + 100.dp,
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = bottomNavBarHeight + 20.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (activeSortOption == SearchSortOptions.MOVIESANDSHOWS ||
            activeSortOption == SearchSortOptions.MOVIE ||
            activeSortOption == SearchSortOptions.SHOW ||
            activeSortOption == SearchSortOptions.GENRE){


            val filtered = searchResultsList.filter {

                it.type == "Movie" || it.type != "Movie"}
            items(filtered) { prod ->

                ProductionCardSearchPage(
                    production = prod,
                    handleProductionClick = handleProductionClick
                )
            }
        } else if (activeSortOption == SearchSortOptions.USER)
        {
            items(userList) { user ->
                //Individual show search result items
                UserCardSearchPage(
                    user = user,
                    handleUserClick = handleUserClick
                )
            }
        }

    }

    TopNavBarSearchPage(
        handleSearchQuery = handleSearchQuery,
        handleSortOptionsChange = handleSortOptionsChange,
        activeSortOption = activeSortOption
    )


}

@Composable
fun TopNavBarSearchPage (
    handleSearchQuery: (sortingOption: SearchSortOptions, searchQuery: String) -> Unit,
    handleSortOptionsChange: (sortOption: SearchSortOptions) -> Unit,
    activeSortOption: SearchSortOptions
){

    var searchQuery by remember { mutableStateOf("") }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf(
        SearchSortOptions.MOVIESANDSHOWS, SearchSortOptions.MOVIE, SearchSortOptions.SHOW,
        SearchSortOptions.GENRE, SearchSortOptions.USER
    )

    var dropDownButtonText by remember {
        mutableStateOf(GenerateSearchOptionName(activeSortOption))
    }


    //Graphics
    Box(
        modifier = Modifier.wrapContentSize()
    ){
        TopNavbarBackground()

        //Search bar and submit button button
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(top = topNavBarContentStart)
        ) {
            //Search bar and button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
            ){
                //Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it},
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .wrapContentHeight(),
                    textStyle = TextStyle(
                        fontSize = headerSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = White,
                    ),
                    label = {
                        //Searchbar label
                        Row(){
                            Text(
                                "Search...",
                                fontSize = headerSize,
                                fontFamily = fontFamily,
                                fontWeight = weightBold,
                                color = White,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                            )
                        }
                    },
                )

                //SearchButton
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .width(80.dp)
                        .padding(top = 8.dp)
                        .background(
                            color = Purple,
                            shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp
                            ))
                        .clickable {
                            //SEARCH BUTTON LOGIC
                            handleSearchQuery(activeSortOption, searchQuery)

                        }
                ){
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(DarkGray),
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.Center)
                    )
                }

            }

            //Category select
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                //Category button
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp)
                        .padding(top = 15.dp)
                        .align(Alignment.Center)
                        .background(
                            color = Gray,
                            shape = RoundedCornerShape(5.dp)
                        )
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

                                    Text(
                                        text = GenerateSearchOptionName(option),
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
                                dropDownButtonText = GenerateSearchOptionName(option)
                                handleSortOptionsChange(option)
                            })
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ProductionCardSearchPage(
    production: Production,
    handleProductionClick: (productionID: String, productionType: String) -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                //Button logic when clicking search result items
            }
            .fillMaxWidth()
    ){
        ProductionImage(
            imageID = production.posterUrl,
            imageDescription = production.title + " Poster",
            modifier = Modifier
                .clickable {
                    handleProductionClick(production.imdbID, production.type)
                }
        )

        Text(
            text = production.title ,
            fontSize = headerSize,
            fontWeight = weightBold,
            fontFamily = fontFamily,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp)
                .align(Alignment.CenterHorizontally)
                .width(150.dp)
        )
    }
}

@Composable
fun UserCardSearchPage(
    user: User,
    handleUserClick: (userID: String) -> Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .clickable {
                handleUserClick(user.id)
            }
    ) {
        ProfileImage(
            imageID = user.profileImageID,
            userName = user.userName,
            sizeMultiplier = 1.5f

        )
        Text(
            text = user.userName,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(150.dp)
        )

    }
}

fun GenerateSearchOptionName (
    searchOption: SearchSortOptions,
): String
{
    if(searchOption== SearchSortOptions.MOVIESANDSHOWS)
    {
        return "Movies & Shows"
    }
    else
    {
        return searchOption.toString().lowercase().replaceFirstChar { it.uppercase() }
    }
}
