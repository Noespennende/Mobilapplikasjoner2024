package com.movielist.model

import com.google.gson.annotations.SerializedName

// Klasse for å holde på data hentet fra API
data class ApiResponse(
// Må Gå gjennom her å fjerne irelevante verdier + sette kommentarer på verdiene som hentes (hva de står for og hva/hvor de brukes)
	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("entries")
	val entries: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null
)

data class TitleText(

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class TitleType(

	@field:SerializedName("isEpisode")
	val isEpisode: Boolean? = null,

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("isSeries")
	val isSeries: Boolean? = null
)

data class OriginalTitleText(

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class Caption(

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("plainText")
	val plainText: String? = null
)

data class ResultsItem(

	@field:SerializedName("titleType")
	val titleType: TitleType? = null,

	@field:SerializedName("primaryImage")
	val primaryImage: PrimaryImage? = null,

	@field:SerializedName("releaseDate")
	val releaseDate: Any? = null,

	@field:SerializedName("originalTitleText")
	val originalTitleText: OriginalTitleText? = null,

	@field:SerializedName("titleText")
	val titleText: TitleText? = null,

	//@field:SerializedName("_id")
	//val id: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("releaseYear")
	val releaseYear: ReleaseYear? = null
)

data class ReleaseDate(

	@field:SerializedName("month")
	val month: Any? = null,

	@field:SerializedName("year")
	val year: Int? = null,

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("day")
	val day: Any? = null
)

data class ReleaseYear(

	@field:SerializedName("year")
	val year: Int? = null,

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("endYear")
	val endYear: Any? = null
)

data class PrimaryImage(

	@field:SerializedName("__typename")
	val typename: String? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("caption")
	val caption: Caption? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)
