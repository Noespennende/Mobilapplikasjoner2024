package com.movielist.data

import androidx.browser.browseractions.BrowserActionsIntent.BrowserActionsItemId
import java.util.Calendar
import java.util.UUID

val userList = mutableListOf<User>()

data class User (
    val id: String = UUID.randomUUID().toString(),
    val userName: String,
    val email: String,
    val friendList: MutableList<User> = mutableListOf(),
    val myReviews: MutableList<Review> = mutableListOf(),
    val favoriteCollection:  MutableList<ListItem> = mutableListOf(),
    val profileImageID: Int,
    val completedShows:  MutableList<ListItem> = mutableListOf(),
    val wantToWatchShows:  MutableList<ListItem> = mutableListOf(),
    val droppedShows:  MutableList<ListItem> = mutableListOf(),
    val currentlyWatchingShows:  MutableList<ListItem> = mutableListOf(),
    val gender: String = "Prefer not to say",
    val location: String = "It's a secret",
    val website: String = "",
    val bio: String = ""
)


fun updateListItemScore(user: User, listType: String, itemId: String, newScore: Int): Boolean {
    val targetList = when(listType){
        "completed" -> user.completedShows
        "wantToWatch" -> user.wantToWatchShows
        "dropped" -> user.droppedShows
        "currentlyWatching" -> user.currentlyWatchingShows
        else -> return false
    }

    val listItem = targetList.find { it.id == itemId }

    return if (listItem != null) {
        listItem.score = newScore
        true
    } else {
        false
    }
}

fun getUniqueShows (user: User): List<ListItem>{
    val allShows = user.completedShows + user.wantToWatchShows + user.droppedShows + user.currentlyWatchingShows

    val uniqueShows = allShows.distinctBy { it.id }

    return uniqueShows
}

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



fun favoriteMoviesInCommon(user: User, friend: User): List<ListItem> {
    val commonMovies: MutableList<ListItem> = mutableListOf()

    val userMovies = user.favoriteCollection
    val friendMovies = friend.favoriteCollection

    userMovies.forEach { movie ->
        friendMovies.forEach { friendMovie ->
            if (movie.id == friendMovie.id) {
                commonMovies.add(movie)
            }
        }
    }
    return commonMovies 
}

fun completedShowsInCommon(user: User, friend: User): List<ListItem> {
    val commonCompleted: MutableList<ListItem> = mutableListOf()

    user.completedShows.filter { userShow ->
        friend.completedShows.any { friendShow -> userShow.id == friendShow.id }
    }.forEach({common ->
        commonCompleted.add(common)
    })
    return commonCompleted
}

fun wantToWatchShowsInCommon(user: User, friend: User): List<ListItem> {
    val commonShows: MutableList<ListItem> = mutableListOf()

    user.wantToWatchShows.filter { userShow ->
        friend.wantToWatchShows.any { friendShow -> userShow.id == friendShow.id }
    }.forEach({commonShow ->
        commonShows.add(commonShow)
    })

    return commonShows
}

fun currentlyWatchShowsInCommon(user: User, friend: User): List<ListItem> {
    val commonShows: MutableList<ListItem> = mutableListOf()

    user.currentlyWatchingShows.filter { userShow ->
        friend.currentlyWatchingShows.any { friendShow -> userShow.id == friendShow.id }
    }.forEach({commonShow ->
        commonShows.add(commonShow)
    })

    return commonShows
}


fun writeReview(reviewer: User, score: Int, production:Production, reviewBody: String,
                postDate: Calendar, likes: Int ) : User{
    val newReview = Review(
        score = score,
        show = production,
        reviewBody = reviewBody,
        postDate = postDate,
        likes = likes,
        reviewer = reviewer
    )

    val updatedReviewList = reviewer.myReviews.toMutableList()

    updatedReviewList.add(newReview)

    return reviewer.copy(myReviews = updatedReviewList)
}

