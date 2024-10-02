package com.movielist.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.movielist.R
import com.movielist.data.Show
import com.movielist.data.searchSortOptions
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.textFieldColors
import com.movielist.ui.theme.topContentStart
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import java.util.Calendar


@Composable
fun SearchPage () {
    //TEMP CODE DELETE THIS

    val showList = mutableListOf<Show>()

    for (i in 0..12) {
        showList.add(
            Show(
                title = "Silo",
                length = 12,
                imageID = R.drawable.silo,
                imageDescription = "Silo TV Show",
                releaseDate = Calendar.getInstance()
            )
        )
    }
    //TEMP CODE DELETE ABOVE


    //Graphics:
    TopNavBarSearchPage()

}

@Composable
fun TopNavBarSearchPage (){

    var searchQuery by remember { mutableStateOf("") }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf(searchSortOptions.NAME, searchSortOptions.MOVIE, searchSortOptions.SHOW,
        searchSortOptions.GENRE, searchSortOptions.USER)
    var dropDownButtonText by remember {mutableStateOf(sortOptions[0].toString())}

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
                .padding(top = topContentStart)
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
                        .height(40.dp)
                        .width(100.dp)
                        .padding(top = 8.dp)
                        .align(Alignment.Center)
                        .background(
                            color = Color.Transparent,
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
                                        text = option.toString(),
                                        fontSize = headerSize,
                                        fontWeight = weightBold,
                                        fontFamily = fontFamily,
                                        color = White,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                    )
                                }
                            },
                            onClick = {
                                //On click logic for dropdown menu
                                dropDownExpanded = false
                                dropDownButtonText = option.toString()
                            })
                        }
                    }
                }
            }

        }
    }
}

