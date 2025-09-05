package com.nvshink.domain.location.model

/**
 * This data class is used to store information about which filtering options have been selected. If option is null, it means that it is not selected.
 * @param name The name of the location.
 * @param type The type of the location.
 * @param dimension The dimension in which the location is located.
 */
data class LocationFilterModel(
    val name: String?,
    val type: String?,
    val dimension: String?,
)
