package com.nvshink.domain.location.repository

import com.nvshink.domain.location.model.LocationFilterModel
import com.nvshink.domain.location.model.LocationModel
import com.nvshink.domain.location.utils.LocationSortFields
import com.nvshink.domain.resource.PageInfoModel
import com.nvshink.domain.resource.Resource
import com.nvshink.domain.resource.SortTypes
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Get list of locations and page if data is remote:
     * @param pageInfoModel Current page information. If data is from cache it is null.
     * @param filterModel Defines which fields and values will be filtered by.
     */
    suspend fun getLocations(
        pageInfoModel: PageInfoModel?,
        filterModel: LocationFilterModel
    ): Flow<Resource<Pair<PageInfoModel?, List<LocationModel>>>>

    /**
     * Get list of locations by list id.
     * @param ids List of Episode id numbers.
     */
    suspend fun getLocationsByIds(ids: List<Int>): Flow<Resource<List<LocationModel>>>
    suspend fun getLocationById(id: Int): Flow<Resource<LocationModel>>
}