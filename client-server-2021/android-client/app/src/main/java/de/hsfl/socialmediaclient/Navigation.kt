package de.hsfl.socialmediaclient

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import de.hsfl.socialmediaclient.models.Account
import de.hsfl.socialmediaclient.screens.PostListScreen
import de.hsfl.socialmediaclient.screens.ProfileScreen
import de.hsfl.socialmediaclient.screens.Screen

/**
 * Navigation
 *
 * Handles navigation
 */
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController= navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            PostListScreen(navController, true)
        }

        composable(
            route = Screen.ProfileScreen.route + "/{account}",
            arguments = listOf(
                navArgument("account") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            it.arguments?.getString("account")?.let { json ->
                val account = Gson().fromJson(json, Account::class.java)

                ProfileScreen(account)
            }
        }
    }
}