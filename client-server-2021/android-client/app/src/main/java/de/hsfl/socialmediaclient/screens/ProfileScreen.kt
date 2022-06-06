package de.hsfl.socialmediaclient.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.hsfl.socialmediaclient.models.Account

/**
 * Profile screen
 *
 * Shows profile of an account
 *
 * @param account the account to be shown
 */
@Composable
fun ProfileScreen(account: Account) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                    ) {
                        Text("Social Media Platform", fontSize = 20.sp)
                    }
                }
            )
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (account.publicProfile) {
                        ProfileContent(account)
                    } else {
                        ProfilePrivate()
                    }
                }
            }
        }
    )
}

/**
 * Profile content
 *
 * Shows the actual content of an account
 *
 * @param account the account from which the content should be displayed
 */
@Composable
fun ProfileContent(account: Account) {
    Text(
        text = "Username: ${account.username}"
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Firstname: ${account.firstname}"
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Lastname: ${account.lastname}"
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Email: ${account.email}"
    )
}

/**
 * Profile private
 *
 * Shows profile private text
 */
@Composable
fun ProfilePrivate() {
    Text("Profile is private!")
}

/**
 * Profile previews
 *
 * Shows preview of profile screen
 */
@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    val account = Account(
        1,
        "MaMu",
        "max.musermann@gmail.com",
        "Max",
        "Mustermann",
        true
    )

    ProfileScreen(account)
}