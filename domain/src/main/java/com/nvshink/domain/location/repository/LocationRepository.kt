package com.nvshink.domain.location.repository

import com.nvshink.domain.character.model.CharacterModel
import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Get list of locations on specified page from API:
     * @param pageInfoModel Current page information.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getLocationsApi(
        pageInfoModel: PageInfoModel,
        filterModel: LocationFilterModel
    ): Flow<Pair<PageInfoModel, Resource<List<LocationModel>>>>

    /**
     * Get list of locations by list id from API.
     * @param ids List of Episode id numbers.
     */
    suspend fun getLocationsByIdsApi(ids: List<Int>): Flow<Resource<List<LocationModel>>>
    /**
     * Get one location by id from API.
     * @param id Episode id number.
     */
    suspend fun getLocationByIdApi(id: Int): Flow<Resource<LocationModel>>
    /**
     * Get list of locations from DB:
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getLocationsDB(
        filterModel: LocationFilterModel
    ): Flow<Resource<List<LocationModel>>>

    /**
     * Get list of locations by list id from DB.
     * @param ids List of Episode id numbers.
     */
    suspend fun getLocationsByIdsDB(ids: List<Int>): Flow<Resource<List<LocationModel>>>
    /**
     * Get one location by id from DB.
     * @param id Episode id number.
     */
    suspend fun getLocationByIdDB(id: Int): Flow<Resource<LocationModel>>
}