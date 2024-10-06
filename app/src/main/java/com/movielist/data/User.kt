package com.movielist.data

import java.util.Calendar
import java.util.UUID

val userList = mutableListOf<User>()

data class User (
    val id: String = UUID.randomUUID().toString(),
    val userName: String,
    val email: String,
    val friendList: List<User> = emptyList(),
    val myReviews: List<Review> = emptyList(),
    val favoriteCollection: List<ListItem> = emptyList(),
    val profileImageID: Int,
    val completedShows: List<ListItem> = emptyList(),
    val wantToWatchShows: List<ListItem> = emptyList(),
    val droppedShows: List<ListItem> = emptyList(),
    val currentlyWatchingShows: List<ListItem> = emptyList()
)


fun deleteUser(uuid: String){
    val userToDelete = userList.find{it.id == uuid}

    if(userToDelete != null){
        userList.remove(userToDelete)
        println("User ${userToDelete.userName} har blitt slettet")
    }else{
        println("Brukeren med ${uuid} ikke funnet")
    }
}

fun addFriend(user: User, friend: User): User{
    val updatedFriendList = user.friendList.toMutableList()

    if(updatedFriendList.find { it.id != friend.id } == null) {
        updatedFriendList.add(friend)
        println("Bruker ${friend.userName} har blitt lagt til vennelisten din :)")
    }else{
        println("Brukeren ${friend.userName} er allerede i vennelisten din:)")
    }


    return user.copy(friendList = updatedFriendList)
}


fun removeFriend(user: User, friend: User) : User{
    val updatedFriendList = user.friendList.toMutableList()

    val friendToRemove = updatedFriendList.find { it.id == friend.id}

    if(friendToRemove != null){
        updatedFriendList.remove(friend)
        println("Brukeren ${friend.userName} har blitt slettet")
    }else{
        println("Brukeren ${friend.userName} finnes ikke i listen din")
    }

    return user.copy(friendList =  updatedFriendList)
}
/* Hvilke filmer skal være common, favorites, completed etc
fun moviesInCommon(user: User, friend: User) : User {
    val commonMovies: List<ListItem> = emptyList()

    val userMovies =
}*/
/* må skrives når vi får inn filmer etc
fun wantToWatchInCommon(user: User, friend: User) : User{
    val commonWatchList: List<ListItem> = emptyList()

    val userWatchList = user.wantToWatchShows.toMutableList()
    val friendWatchList = friend.wantToWatchShows.toMutableList()


    }
}*/

fun writeReview(reviewer: User, score: Int, show:Show, reviewBody: String,
                postDate: Calendar, likes: Int ) : User{
    val newReview = Review(
        score = score,
        show = show,
        reviewBody = reviewBody,
        postDate = postDate,
        likes = likes,
        reviewer = reviewer
    )

    val updatedReviewList = reviewer.myReviews.toMutableList()

    updatedReviewList.add(newReview)

    return reviewer.copy(myReviews = updatedReviewList)
}

