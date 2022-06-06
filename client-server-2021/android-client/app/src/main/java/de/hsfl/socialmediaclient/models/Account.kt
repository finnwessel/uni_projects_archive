package de.hsfl.socialmediaclient.models

import kotlinx.serialization.Serializable

/**
 * Account model
 *
 * @property id the id of an account
 * @property username the username of an account
 * @property firstname the firstname of an account
 * @property lastname the lastname of an account
 * @property email the email of an account
 * @property publicProfile represents whether account is public or not
 */
@Serializable
data class Account(
    val id: Long,
    val username: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val publicProfile: Boolean
)
