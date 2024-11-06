package com.movielist.model

import java.util.Calendar

val userList = mutableListOf<User>()

data class User (
    val id: String,  //= UUID.randomUUID().toString(),
    val userName: String,
    val email: String,
    val profileImageID: Int,
    val gender: String = "Prefer not to say",
    val location: String = "It's a secret",
    val website: String = "",
    val bio: String = "",
    val friendList: MutableList<String> = mutableListOf(),
    val myReviews: MutableList<Review> = mutableListOf(),
    val favoriteCollection:  MutableList<ListItem> = mutableListOf(),
    val completedCollection:  MutableList<ListItem> = mutableListOf(),
    val wantToWatchCollection:  MutableList<ListItem> = mutableListOf(),
    val droppedCollection:  MutableList<ListItem> = mutableListOf(),
    val currentlyWatchingCollection:  MutableList<ListItem> = mutableListOf()
)

fun movieGenrePercentage(user: User): Map<String, Double> {
    val allFilms = getAllMovies(user)
    val genreCounter = mutableMapOf<String, Int>()
    var totalGenreCount = 0

    allFilms.forEach { listItem ->
        val movie = listItem.production as? Movie
        movie?.let {

            it.genre.forEach { genre ->
                genreCounter[genre] = genreCounter.getOrDefault(genre, 0) + 1
                totalGenreCount++
            }
        }
    }

    val sortedGenres = genreCounter.entries.sortedByDescending { it.value }.take(4)


    val genrePercentage = mutableMapOf<String, Double>()

    sortedGenres.forEach { (genre, count) ->
        genrePercentage[genre] = (count.toDouble() / totalGenreCount) * 100
    }

    val remainingGenresCount = genreCounter.size - sortedGenres.size

    if (remainingGenresCount > 0) {
        genrePercentage["any"] = (remainingGenresCount.toDouble() / totalGenreCount) * 100
    }

    return genrePercentage
}


fun showGenrePercentage(user: User): Map<String, Double> {
    val allShows = getAllshows(user)
    val genreCounter = mutableMapOf<String, Int>()
    var totalGenreCount = 0

    allShows.forEach { listItem ->
        val show = listItem.production as? Movie
        show?.let {
            it.genre.forEach { genre ->
                genreCounter[genre] = genreCounter.getOrDefault(genre, 0) + 1
                totalGenreCount++
            }
        }
    }


    val sortedGenres = genreCounter.entries.sortedByDescending { it.value }.take(4)


    val genrePercentage = mutableMapOf<String, Double>()

    sortedGenres.forEach { (genre, count) ->
        genrePercentage[genre] = (count.toDouble() / totalGenreCount) * 100// Round down to two decimal places
    }


    val remainingGenresCount = genreCounter.size - sortedGenres.size

    if (remainingGenresCount > 0) {
        genrePercentage["any"] = (remainingGenresCount.toDouble() / totalGenreCount) * 100 // Round down to two decimal places
    }

    return genrePercentage
}


fun updateListItemScore(user: User, listType: String, itemId: String, newScore: Int): Boolean {
    val targetList = when(listType){
        "completed" -> user.completedCollection
        "wantToWatch" -> user.wantToWatchCollection
        "dropped" -> user.droppedCollection
        "currentlyWatching" -> user.currentlyWatchingCollection
        else -> return false
    }

    val listItem = targetList.find { it.id == itemId }

    return if (listItem != null) {
        listItem.score = newScore
        listItem.lastUpdated = Calendar.getInstance()
        true
    } else {
        false
    }
}

fun isMovie(production: Production): Boolean {
    return production.type.lowercase() == "movie"
}

fun getAllMoviesAndShows(user: User): List<ListItem> {
    val allShows = user.completedCollection + user.wantToWatchCollection + user.droppedCollection + user.currentlyWatchingCollection
    return allShows
}

fun getAllMovies(user: User): List<ListItem> {
    var allShows = getAllMoviesAndShows(user)

    val finishedList = allShows.distinctBy { it.id }
    val onlyMovies = finishedList.filter { listItem ->
        isMovie(listItem.production as Movie)
    }

    return onlyMovies
}

fun getAllshows(user: User): List<ListItem> {
    var allShows = getAllMoviesAndShows(user)

    val finishedList = allShows.distinctBy { it.id }
    val onlyShows = finishedList.filter { listItem ->
        !isMovie(listItem.production)
    }

    return onlyShows
}

fun getUniqueShows (user: User): List<ListItem>{
    val allShows = user.completedCollection + user.wantToWatchCollection + user.droppedCollection + user.currentlyWatchingCollection

    val uniqueShows = allShows.distinctBy { it.id }

     println("test")
    return  uniqueShows
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

/*
fun addFriend(user: User, friend: User): User {
    val updatedFriendList = user.friendList.toMutableList()


    if (updatedFriendList.none { it.id == friend.id }) {
        updatedFriendList.add(friend)
        println("Bruker ${friend.userName} har blitt lagt til vennelisten din :)")
    } else {
        println("Brukeren ${friend.userName} er allerede i vennelisten din :)")
    }

    // Return user with updated friend list
    return user.copy(friendList = updatedFriendList)
}


fun removeFriend(user: User, friend: User) : User {
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
 */



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

    user.completedCollection.filter { userShow ->
        friend.completedCollection.any { friendShow -> userShow.id == friendShow.id }
    }.forEach({common ->
        commonCompleted.add(common)
    })
    return commonCompleted
}

fun wantToWatchShowsInCommon(user: User, friend: User): List<ListItem> {
    val commonShows: MutableList<ListItem> = mutableListOf()

    user.wantToWatchCollection.filter { userShow ->
        friend.wantToWatchCollection.any { friendShow -> userShow.id == friendShow.id }
    }.forEach({commonShow ->
        commonShows.add(commonShow)
    })

    return commonShows
}

fun currentlyWatchShowsInCommon(user: User, friend: User): List<ListItem> {
    val commonShows: MutableList<ListItem> = mutableListOf()

    user.currentlyWatchingCollection.filter { userShow ->
        friend.currentlyWatchingCollection.any { friendShow -> userShow.id == friendShow.id }
    }.forEach({commonShow ->
        commonShows.add(commonShow)
    })

    return commonShows
}


fun writeReview(reviewer: User, score: Int, production: Production, reviewBody: String,
                postDate: Calendar, likes: Int ) : User {
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

