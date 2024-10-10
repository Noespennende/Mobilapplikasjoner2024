package backend

import androidx.lifecycle.ViewModel
import com.movielist.R
import com.movielist.data.ListItem
import com.movielist.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import kotlin.random.Random

class QueryViewModel : ViewModel() {
    private val _watchingCollection = MutableStateFlow<List<String>>(emptyList())
    val watchingCollection: StateFlow<List<String>> = _watchingCollection

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError

    // Funksjon for å hente brukers watching collection og konvertere IMDB-IDs til ListItem
    fun fetchUserWatchingCollection(userID: String) {

        _isLoading.value = true
        _hasError.value = false

        getUsersWatchingCollection(
            userID,
            onSuccess = { collection ->
                _watchingCollection.value = collection
                _isLoading.value = false
            },
            onFailure = { exception ->
                _hasError.value = true
                _isLoading.value = false
            });
    }

    // Funksjon som konverterer raw IMDB-IDs til en liste av ListItem
    fun createProductionListItems(prodCollection: List<String>): List<ListItem> {
        val productionListItemList = mutableListOf<ListItem>()
        for (prod in prodCollection) {
            val listItem = ListItem(
                // Modifisere her basert på API kall og sjekk av hvilken Production-type dataen er
                // KUN DUMMY-DATA FOR NÅ
                production = Movie(
                    imdbID = prod,  // Bruker IMDB-IDs fra collection
                    title = "Silo",  // Dynamisk data kan hentes basert på prod (eventuelt API kall)
                    description = "TvShow Silo description here",
                    genre = "Action",
                    releaseDate = Calendar.getInstance(),
                    actors = emptyList(),
                    rating = 4,
                    reviews = ArrayList(),
                    posterUrl = R.drawable.silo,
                    lengthMinutes = 127,
                    trailerUrl = "trailerurl.com"
                ),
                currentEpisode = 0,
                score = Random.nextInt(0, 10)
            )
            productionListItemList.add(listItem)
        }
        return productionListItemList
    }
}

