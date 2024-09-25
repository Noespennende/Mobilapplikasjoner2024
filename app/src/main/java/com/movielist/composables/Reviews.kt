package com.movielist.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movielist.data.Review
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.verticalPadding
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import java.util.Calendar

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
            imageID = review.show.imageID
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
                                color = White
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
                    color = White,
                    modifier = Modifier
                        .fillMaxWidth(.8f)
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
