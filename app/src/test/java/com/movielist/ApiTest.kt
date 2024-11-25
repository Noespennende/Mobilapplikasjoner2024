package com.movielist

import com.movielist.networking.ApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiTest {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/") // Replace with your actual base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    @Test
    fun `getAllMedia returns 200`() = runBlocking {
        val response = apiService.getAllMedia().execute()
        assertEquals(200, response.code())
        assert(response.body()?.results?.isNotEmpty() == true)
    }

    @Test
    fun `getMovie returns 200 for valid movie ID`() = runBlocking {
        val validMovieId = "933260"
        val response = apiService.getMovie(validMovieId).execute()
        assertEquals(200, response.code())
        assert(response.body()?.id == validMovieId.toInt())
    }

    @Test
    fun `getMovie returns 404 for invalid movie ID`() = runBlocking {
        val invalidMovieId = "99999999"
        val response = apiService.getMovie(invalidMovieId).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getShow returns 200 for valid show ID`() = runBlocking {
        val validShowId = "94722"
        val response = apiService.getShow(validShowId).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getShow returns 404 for invalid show ID`() = runBlocking {
        val invalidShowId = "99999999"
        val response = apiService.getShow(invalidShowId).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getShowSeason returns 200 for valid show ID and season number`() = runBlocking {
        val validShowId = "94722"
        val validSeasonNumber = "1"
        val response = apiService.getShowSeason(validShowId, validSeasonNumber).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getShowSeason returns 404 for invalid show ID and season number`() = runBlocking {
        val invalidShowId = "99999999"
        val invalidSeasonNumber = "1000"
        val response = apiService.getShowSeason(invalidShowId, invalidSeasonNumber).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getShowEpisode returns 200 for valid show ID and season number and episode number`() = runBlocking {
        val validShowId = "94722"
        val validSeasonNumber = "1"
        val validEpisodeNumber = "1"
        val response = apiService.getShowEpisode(validShowId, validSeasonNumber, validEpisodeNumber).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getShowEpisode returns 404 for invalid show ID and season number and episode number`() = runBlocking {
        val invalidShowId = "99999999"
        val invalidSeasonNumber = "1000"
        val invalidEpisodeNumber = "1000"
        val response = apiService.getShowEpisode(invalidShowId, invalidSeasonNumber, invalidEpisodeNumber).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getMovieCredits returns 200 for valid movie ID`() = runBlocking {
        val validMovieId = "933260"
        val response = apiService.getMovieCredits(validMovieId).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getMovieCredits returns 404 for invalid movie ID`() = runBlocking {
        val invalidMovieId = "99999999"
        val response = apiService.getMovieCredits(invalidMovieId).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getShowCredits returns 200 for valid show ID`() = runBlocking {
        val validShowId = "94722"
        val response = apiService.getShowCredits(validShowId).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getShowCredits returns 404 for invalid show ID`() = runBlocking {
        val invalidShowId = "99999999"
        val response = apiService.getShowCredits(invalidShowId).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getMovieVideo returns 200 for valid movie ID`() = runBlocking {
        val validMovieId = "933260"
        val response = apiService.getMovieVideo(validMovieId).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getMovieVideo returns 404 for invalid movie ID`() = runBlocking {
        val invalidMovieId = "99999999"
        val response = apiService.getMovieVideo(invalidMovieId).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `getShowVideo returns 200 for valid show ID`() = runBlocking {
        val validShowId = "94722"
        val response = apiService.getShowVideo(validShowId).execute()
        assertEquals(200, response.code())
    }

    @Test
    fun `getShowVideo returns 404 for invalid show ID`() = runBlocking {
        val invalidShowId = "99999999"
        val response = apiService.getShowVideo(invalidShowId).execute()
        assertEquals(404, response.code())
    }

    @Test
    fun `searchMulti returns 200 with query`() = runBlocking {
        val query = "cool"
        val response = apiService.searchMulti(query).execute()
        assertEquals(200, response.code())
        assert(response.body()?.results?.isNotEmpty() == true)
    }

    @Test
    fun `searchMulti returns 200 with empty query`() = runBlocking {
        val emptyQuery = ""
        val response = apiService.searchMulti(emptyQuery).execute()
        assertEquals(200, response.code())
        assert(response.body() != null)
    }

    @Test
    fun `searchMulti returns empty result with empty query`() = runBlocking {
        val emptyQuery = ""
        val response = apiService.searchMulti(emptyQuery).execute()
        assertEquals(200, response.code())
        assert(response.body()?.results?.isEmpty() == true)
    }
}
