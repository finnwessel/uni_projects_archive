package de.hsfl.socialmediaclient.services

import de.hsfl.socialmediaclient.models.Post
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/**
 * Post service
 *
 * Used to fetch posts from api
 */
class PostService {

    companion object {
        var apiUrl: String = "http://10.0.2.2:8080/" //Localhost
    }

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun getPosts(): Result<List<Post>> {
        return try {
            val response = apiService.getPosts()
            success(response)
        } catch (e: Exception) {
            failure(e)
        }
    }
}

/**
 * Api service
 *
 * Represents api service
 */
interface ApiService {
    /**
     * Gets all posts from api
     */
    @GET("app/api/post")
    suspend fun getPosts(): List<Post>
}