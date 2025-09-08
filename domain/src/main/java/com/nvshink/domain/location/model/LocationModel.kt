package com.nvshink.domain.location.model

import java.time.ZonedDateTime

/**
 * @param id The id of the location.
 * @param name The name of the location.
 * @param type The type of the location.
 * @param dimension The dimension in which the location is located.
 * @param residents List of character links who have been last seen in the location.
 * @param url Link to the location's own endpoint.
 * @param created Time at which the location was created in the database.
 */
data class LocationModel(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: ZonedDateTime
)
