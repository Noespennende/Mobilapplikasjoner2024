package com.movielist.composables

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.movielist.R
import com.movielist.data.SearchSortOptions
import com.movielist.data.Show
import com.movielist.ui.theme.*
import java.util.Calendar


@Composable
fun SearchPage () {
    //TEMP CODE DELETE THIS

    val showList = mutableListOf<Show>()

    for (i in 0..50) {
        showList.add(
            Show(
                title = "The lord of the rings: The return of the king",
                length = 12,
                imageID = R.drawable.silo,
                imageDescription = "Silo TV Show",
                releaseDate = Calendar.getInstance()
            )
        )
    }
    //TEMP CODE DELETE ABOVE


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
        items(showList) { show ->
            //Individual show search result items
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        //Button logic when clicking search result items
                    }
                    .fillMaxWidth()
            ){
                ShowImage(
                    imageID = show.imageID,
                    imageDescription = show.imageDescription
                )

                Text(
                    text = show.title,
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

        }
    }


    TopNavBarSearchPage()


}

@Composable
fun TopNavBarSearchPage (){

    //Nesessary variables
    var searchQuery by remember { mutableStateOf("") }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf(
        SearchSortOptions.MOVIESANDSHOWS, SearchSortOptions.MOVIE, SearchSortOptions.SHOW,
        SearchSortOptions.GENRE, SearchSortOptions.USER)
    var dropDownButtonText by remember {
        mutableStateOf(GenerateSearchOptionName(sortOptions[0]))
    }


    //Graphics
    Box(
        modifier = Modifier.wrapContentSize()
    ){
        TopNavbarBackground(
        )
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
                            })
                        }
                    }
                }
            }

        }
    }
}


fun GenerateSearchOptionName (
    searchOption: SearchSortOptions,
): String
{
    if(searchOption== SearchSortOptions.MOVIESANDSHOWS)
    {
        return "Movies and Shows"
    }
    else
    {
        return searchOption.toString().lowercase().replaceFirstChar { it.uppercase() }
    }
}
