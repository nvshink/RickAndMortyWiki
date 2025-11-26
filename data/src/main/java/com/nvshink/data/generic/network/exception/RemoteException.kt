package com.nvshink.data.generic.network.exception

import com.nvshink.data.R
import io.ktor.http.HttpStatusCode

abstract class HttpException(
    override val message: String,
    val statusCode: Int,
    cause: Throwable? = null
) : Exception(message, cause) {
    val errorCode: String
        get() = "HTTP_ERROR_$statusCode"
}

class ResourceNotFoundException(
    message: String = "Resource not found",
    cause: Throwable? = null
) : HttpException(message, HttpStatusCode.NotFound.value, cause)