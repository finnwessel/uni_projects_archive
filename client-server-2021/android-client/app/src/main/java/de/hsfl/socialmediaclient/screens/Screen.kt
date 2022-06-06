package de.hsfl.socialmediaclient.screens

/**
 * Screen
 *
 * Represents all screens / routes
 *
 * @property route the name of the route
 */
sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object ProfileScreen : Screen("profile_screen")
}
