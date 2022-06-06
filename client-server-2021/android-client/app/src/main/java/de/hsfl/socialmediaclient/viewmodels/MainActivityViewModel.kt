package de.hsfl.socialmediaclient.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hsfl.socialmediaclient.models.Post
import de.hsfl.socialmediaclient.services.PostService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.Result.Companion.success

/**
 * Main activity view model
 *
 * Manages and stores ui-related daa
 */
class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val postService = PostService()

    private val _posts = MutableStateFlow(success(emptyList<Post>()))
    val posts: StateFlow<Result<List<Post>>> = _posts

    /**
     * Loads all posts
     */
    fun loadPosts() = effect {
        _isLoading.value = true
        _posts.value = postService.getPosts()
        _isLoading.value = false
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Runs function in view model scope
     */
    private fun effect(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }
}