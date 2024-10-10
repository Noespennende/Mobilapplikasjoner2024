package com.movielist.data

import java.util.Calendar

/*
data class Show (
    val title: String,
    val length: Int,
    val imageID: String,
    val releaseDate: Calendar,
    val imageDescription: String,
    val email: String //lagt til for test av annen api
)
*/
// Data classes nedenunder er tilpasset/lagd for dataen vi får fra APIet for filmer
data class MovieResponse(
    val page: Int?,
    val next: String?,
    val entries: Int?,
    val results: List<Movie>
)

data class ShowResponse (
    val page: Int?,
    val next: String?,
    val entries: Int?,
    val results: List<Show>
)

data class Movie(
    val _id: String,
    val id: String,
    val primaryImage: PrimaryImage?,
    val titleType: TitleType,
    val titleText: TitleText,
    val originalTitleText: TitleText?,
    val releaseYear: ReleaseYear?,
    val showLength: Int?, // Må tilpasses
    val currentEpisode: Int? // Må tilpasses
)

data class Show(
    val _id: String,
    val id: String,
    val primaryImage: PrimaryImage?,
    val titleType: TitleType,
    val titleText: TitleText,
    val originalTitleText: TitleText?,
    val releaseYear: ReleaseYear?,
    val showLength: Int?, // Må tilpasses
    val currentEpisode: Int? // Må tilpasses
)

data class CombinedData(
    val _id: String,
    val id: String?,
    val primaryImage: PrimaryImage?,
    val titleType: TitleType?,
    val titleText: TitleText?,
    val originalTitleText: TitleText?,
    //val releaseYear: ReleaseYear?,
    val showLength: Int?, // Må tilpasses
    val totalEpisodes: Int?,
    val currentEpisode: Int? // Må tilpasses
)

data class SeriesDetailsResponse(
    val results: List<Episode>
)

data class Episode(
    val tconst: String,
    val seasonNumber: Int,
    val episodeNumber: Int
)

data class PrimaryImage(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String,
    //val caption: Caption
)

data class Caption(
    val plainText: String
)

data class TitleType(
    val isSeries: Boolean,
    val isEpisode: Boolean
)

data class TitleText(
    val text: String
)

data class ReleaseYear(
    val year: Int,
    val endYear: Int?
)

/* Oppsett på json koden - fikk det via Postman extension (https://www.postman.com/downloads/)
{
    "page": 1,
    "next": "/titles?page=2",
    "entries": 10,
    "results": [
        {
            "_id": "61e57fd65c5338f43c777f4a",
            "id": "tt0000081",
            "primaryImage": {
                "id": "rm211543552",
                "width": 226,
                "height": 300,
                "url": "https://m.media-amazon.com/images/M/MV5BM2ZlYjA4NmItZTYxYy00MGFiLTg3MWUtNzZmYjE1ODZmMThjXkEyXkFqcGdeQXVyNTI2NTY2MDI@._V1_.jpg",
                "caption": {
                    "plainText": "Les haleurs de bateaux (1896)",
                    "__typename": "Markdown"
                },
                "__typename": "Image"
            },
            "titleType": {
                "text": "Short",
                "id": "short",
                "isSeries": false,
                "isEpisode": false,
                "__typename": "TitleType"
            },
            "titleText": {
                "text": "Les haleurs de bateaux",
                "__typename": "TitleText"
            },
            "originalTitleText": {
                "text": "Les haleurs de bateaux",
                "__typename": "TitleText"
            },
            "releaseYear": {
                "year": 1896,
                "endYear": null,
                "__typename": "YearRange"
            },
            "releaseDate": null
        },
        {
            "_id": "61e57fd65c5338f43c777f4c",
            "id": "tt0000045",
            "primaryImage": {
                "id": "rm362538496",
                "width": 226,
                "height": 300,
                "url": "https://m.media-amazon.com/images/M/MV5BNzBjZjI4YjYtNGIyOC00ZDQyLTg0OTctN2U2YmUyMjJiZTQzXkEyXkFqcGdeQXVyNTI2NTY2MDI@._V1_.jpg",
                "caption": {
                    "plainText": "Les blanchisseuses (1896)",
                    "__typename": "Markdown"
                },
                "__typename": "Image"
            },
            "titleType": {
                "text": "Short",
                "id": "short",
                "isSeries": false,
                "isEpisode": false,
                "__typename": "TitleType"
            },
            "titleText": {
                "text": "Les blanchisseuses",
                "__typename": "TitleText"
            },
            "originalTitleText": {
                "text": "Les blanchisseuses",
                "__typename": "TitleText"
            },
            "releaseYear": {
                "year": 1896,
                "endYear": null,
                "__typename": "YearRange"
            },
            "releaseDate": {
                "day": null,
                "month": null,
                "year": 1896,
                "__typename": "ReleaseDate"
            }
        ]
    }
 */
