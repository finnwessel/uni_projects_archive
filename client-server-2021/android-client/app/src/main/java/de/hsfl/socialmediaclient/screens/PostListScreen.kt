package de.hsfl.socialmediaclient.screens

import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import de.hsfl.socialmediaclient.models.Post
import de.hsfl.socialmediaclient.models.Account
import de.hsfl.socialmediaclient.viewmodels.MainActivityViewModel

/**
 * Post list screen
 *
 * Shows a list of posts
 *
 * @param navController the controller used to switch between screens
 * @param fetchPosts indicates whether the posts should be fetched when showing the screen
 */
@Composable
fun PostListScreen(navController: NavController, fetchPosts: Boolean) {
    val viewModel: MainActivityViewModel = viewModel()

    if (fetchPosts) {
        viewModel.loadPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Social Media Platform", fontSize = 20.sp)

                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                viewModel.loadPosts()
                            }
                        )
                    }
                }
            )
        },
        content = {
            val isLoading: Boolean by viewModel.isLoading.collectAsState()

            if (isLoading) {
                PostsLoading()
            } else {
                val posts by viewModel.posts.collectAsState()

                posts.onSuccess {
                    PostListScreenContent(navController, it)
                }.onFailure {
                    PostsError()
                }
            }
        }
    )
}

/**
 * Post list screen content
 *
 * Shows the content of the post list screen
 *
 * @param navController the controller used to switch between screens
 * @param posts the posts shown by this screen
 */
@Composable
fun PostListScreenContent(navController: NavController, posts: List<Post>) {
    LazyColumn {
        items(posts) { post ->
            Post(navController, post)
        }
    }
}

/**
 * Posts loading
 *
 * Shows loading text
 */
@Composable
fun PostsLoading() {
    Text("Loading posts...")
}

/**
 * Post
 *
 * Shows single post
 *
 * @param navController the controller used to switch between screens
 * @param post the post shown by this screen
 */
@Composable
fun Post(navController: NavController, post: Post) {
    fun navigateToAccount(account: Account) {
        val accountJson = Gson().toJson(account)

        navController.navigate(Screen.ProfileScreen.route + "/$accountJson")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = post.account.username
                )

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        navigateToAccount(post.account)
                    }
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                text = post.createdAt
            )

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(10.dp))
            AndroidView(
                factory = { context -> TextView(context) },
                update = { it.text = HtmlCompat.fromHtml(post.content, HtmlCompat.FROM_HTML_MODE_COMPACT) }
            )
        }
    }
}

/**
 * Post error
 *
 * Shows error text
 */
@Composable
fun PostsError() {
    Text(text = "Couldn't fetch posts")
}