package com.nvshink.data.generic.network.di

import com.nvshink.data.generic.network.exception.ResourceNotFoundException
import com.nvshink.data.generic.network.response.ErrorResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            HttpResponseValidator {
                validateResponse { response ->
                    when (response.status) {
                        HttpStatusCode.NotFound -> throw ResourceNotFoundException(response.body<ErrorResponse>().error)
                        else -> Unit
                    }
                }
            }

            defaultRequest {
                url(BASE_URL)
                header("Content-Type", "application/json")
            }

            engine {
                config {
                    connectTimeout(5, TimeUnit.SECONDS)
                    readTimeout(5, TimeUnit.SECONDS)
                }
            }
        }
    }

}