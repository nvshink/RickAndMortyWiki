package com.nvshink.domain.resource

sealed class Resource<out T> {
    data class Loading<T>(val data: T? = null) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(
        val message: String? = null,
        val exception: Exception? = null,
        val data: T? = null
    ) : Resource<T>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default

    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String?, Exception?) -> Unit): Resource<T> {
        if (this is Error) action(message, exception)
        return this
    }

    inline fun onLoading(action: (T?) -> Unit): Resource<T> {
        if (this is Loading) action(data)
        return this
    }

    inline fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, exception)
        is Loading -> Loading(data?.let(transform))
    }

    inline fun <R> fold(
        onLoading: (T?) -> R,
        onSuccess: (T) -> R,
        onError: (String?, Exception?) -> R
    ): R = when (this) {
        is Loading -> onLoading(data)
        is Success -> onSuccess(data)
        is Error -> onError(message, exception)
    }
}