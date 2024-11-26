package com.movielist.model

import com.google.gson.annotations.SerializedName

sealed class ApiProductionResponse



data class ApiAllMediaResponse(

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("total_pages")
	val totalPages: Int? = null,

	@field:SerializedName("results")
	val results: List<AllMedia?>? = null,

	@field:SerializedName("total_results")
	val totalResults: Int? = null
)

data class AllMedia(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("original_title")
	val originalTitle: String? = null,

	@field:SerializedName("original_name")
	val originalName: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null,

	@field:SerializedName("backdrop_path")
	val backdropPath: String? = null,

	@field:SerializedName("media_type")
	val mediaType: String? = null,  // "movie" or "tv"

	@field:SerializedName("original_language")
	val originalLanguage: String? = null,

	@field:SerializedName("genre_ids")
	val genreIds: List<Int>? = null,

	@field:SerializedName("genres")
	val genres: List<Genre>? = null,

	@field:SerializedName("release_date")
	val releaseDate: String? = null,

	@field:SerializedName("first_air_date")
	val firstAirDate: String? = null,

	@field:SerializedName("origin_country")
	val originCountry: List<String>? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("video")
	val video: Boolean? = null
)


data class Genre(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class ApiEpisodeResponse(

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("total_pages")
	val totalPages: Int? = null,

	@field:SerializedName("results")
	val results: List<ApiEpisode?>? = null,

	@field:SerializedName("total_results")
	val totalResults: Int? = null
)

data class ApiEpisode(

	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("episode_number")
	val episodeNumber: Int? = null,

	@field:SerializedName("episode_type")
	val episodeType: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("production_code")
	val productionCode: String? = null,

	@field:SerializedName("runtime")
	val runtime: Int? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("show_id")
	val showId: Int? = null,

	@field:SerializedName("still_path")
	val stillPath: String? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("crew")
	val crew: List<String>? = null,

	@field:SerializedName("guest_stars")
	val guestStars: List<String>? = null
)

data class ApiMovieResponse(

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("backdrop_path")
	val backdropPath: String? = null,

	@field:SerializedName("belongs_to_collection")
	val belongsToCollection: Any? = null,

	@field:SerializedName("budget")
	val budget: Long? = null,

	@field:SerializedName("genres")
	val genres: List<Genre>? = null,

	@field:SerializedName("homepage")
	val homepage: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("imdb_id")
	val imdbId: String? = null,

	@field:SerializedName("origin_country")
	val originCountry: List<String>? = null,

	@field:SerializedName("original_language")
	val originalLanguage: String? = null,

	@field:SerializedName("original_title")
	val originalTitle: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null,

	@field:SerializedName("production_companies")
	val productionCompanies: List<ProductionCompany>? = null,

	@field:SerializedName("production_countries")
	val productionCountries: List<ProductionCountry>? = null,

	@field:SerializedName("release_date")
	val releaseDate: String? = null,

	@field:SerializedName("revenue")
	val revenue: Long? = null,

	@field:SerializedName("runtime")
	val runtime: Int? = null,

	@field:SerializedName("spoken_languages")
	val spokenLanguages: List<SpokenLanguage>? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("tagline")
	val tagline: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("video")
	val video: Boolean? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

)

data class ApiMovieCreditResponse(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("cast")
	val cast: List<MovieCastMember>? = null
)

data class MovieCastMember(

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("gender")
	val gender: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("known_for_department")
	val knownForDepartment: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("original_name")
	val originalName: String? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("profile_path")
	val profilePath: String? = null,

	@field:SerializedName("cast_id")
	val castId: Int? = null,

	@field:SerializedName("character")
	val character: String? = null,

	@field:SerializedName("credit_id")
	val creditId: String? = null,

	@field:SerializedName("order")
	val order: Int? = null
)

data class MovieResponse(
	val movieData: ApiMovieResponse,
	val movieVideoData: List<VideoResult>?,
	val movieCreditData: ApiMovieCreditResponse
): ApiProductionResponse()

data class ShowResponse(
	val showData: ApiShowResponse,
	val showVideoData: List<VideoResult>?,
	val showCreditData: ApiShowCreditResponse
): ApiProductionResponse()

data class ApiMediaVideoResponse(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("results")
	val results: List<VideoResult>? = null
)

data class VideoResult(

	@field:SerializedName("iso_639_1")
	val iso6391: String? = null,

	@field:SerializedName("iso_3166_1")
	val iso3166_1: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("key")
	val key: String? = null,

	@field:SerializedName("site")
	val site: String? = null,

	@field:SerializedName("size")
	val size: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("official")
	val official: Boolean? = null,

	@field:SerializedName("published_at")
	val publishedAt: String? = null,

	@field:SerializedName("id")
	val videoId: String? = null
)

data class ProductionCompany(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("logo_path")
	val logoPath: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("origin_country")
	val originCountry: String? = null
)

data class ProductionCountry(
	@field:SerializedName("iso_3166_1")
	val iso3166_1: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class SpokenLanguage(
	@field:SerializedName("english_name")
	val englishName: String? = null,

	@field:SerializedName("iso_639_1")
	val iso6391: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)


data class ApiShowResponse(

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("backdrop_path")
	val backdropPath: String? = null,

	@field:SerializedName("created_by")
	val createdBy: List<Any?>? = null,

	@field:SerializedName("episode_run_time")
	val episodeRunTime: List<Int?>? = null,

	@field:SerializedName("first_air_date")
	val firstAirDate: String? = null,

	@field:SerializedName("genres")
	val genres: List<Genre?>? = null,

	@field:SerializedName("homepage")
	val homepage: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("in_production")
	val inProduction: Boolean? = null,

	@field:SerializedName("languages")
	val languages: List<String?>? = null,

	@field:SerializedName("last_air_date")
	val lastAirDate: String? = null,

	@field:SerializedName("last_episode_to_air")
	val lastEpisodeToAir: ShowEpisode? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("next_episode_to_air")
	val nextEpisodeToAir: ShowEpisode? = null,

	@field:SerializedName("number_of_episodes")
	val numberOfEpisodes: Int? = null,

	@field:SerializedName("number_of_seasons")
	val numberOfSeasons: Int? = null,

	@field:SerializedName("origin_country")
	val originCountry: List<String?>? = null,

	@field:SerializedName("original_language")
	val originalLanguage: String? = null,

	@field:SerializedName("original_name")
	val originalName: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null,

	@field:SerializedName("seasons")
	val seasons: List<Season?>? = null
)

data class ApiShowCreditResponse (
	@field:SerializedName("cast")
	val cast: List<ShowCastMember>? = null
)

data class ShowCastMember(

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("gender")
	val gender: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("known_for_department")
	val knownForDepartment: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("original_name")
	val originalName: String? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("profile_path")
	val profilePath: String? = null,

	@field:SerializedName("roles")
	val roles: List<Role>? = null,

	@field:SerializedName("total_episode_count")
	val totalEpisodeCount: Int? = null,

	@field:SerializedName("order")
	val order: Int? = null
)

data class Role(

	@field:SerializedName("credit_id")
	val creditId: String? = null,

	@field:SerializedName("character")
	val character: String? = null,

	@field:SerializedName("episode_count")
	val episodeCount: Int? = null
)

data class ShowEpisode(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("episode_number")
	val episodeNumber: Int? = null,

	@field:SerializedName("episode_type")
	val episodeType: String? = null,

	@field:SerializedName("production_code")
	val productionCode: String? = null,

	@field:SerializedName("runtime")
	val runtime: Int? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("show_id")
	val showId: Int? = null,

	@field:SerializedName("still_path")
	val stillPath: String? = null
)

data class Season(
	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("episode_count")
	val episodeCount: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null
)

data class ApiShowSeason(

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("episodes")
	val episodes: List<ShowSeasonEpisode>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null
)

data class ShowSeasonEpisode(
	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("episode_number")
	val episodeNumber: Int? = null,

	@field:SerializedName("episode_type")
	val episodeType: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("production_code")
	val productionCode: String? = null,

	@field:SerializedName("runtime")
	val runtime: Int? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("show_id")
	val showId: Int? = null,

	@field:SerializedName("still_path")
	val stillPath: String? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("crew")
	val crew: List<Any?>? = null,

	@field:SerializedName("guest_stars")
	val guestStars: List<Any?>? = null
)

data class ApiShowSeasonEpisode (
	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("crew")
	val crew: List<Any?>? = null,

	@field:SerializedName("episode_number")
	val episodeNumber: Int? = null,

	@field:SerializedName("guest_stars")
	val guestStars: List<Any?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("production_code")
	val productionCode: String? = null,

	@field:SerializedName("runtime")
	val runtime: Int? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("still_path")
	val stillPath: String? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null
)