package de.hsfl.socialmediaclient.models

import kotlinx.serialization.Serializable

/**
 * Post model
 *
 * @property id the id of a post
 * @property content the content of a post
 * @property createdAt the time a post was created at
 * @property account the author of a post
 */
@Serializable
data class Post(
    val id: Long,
    val content: String,
    val createdAt: String,
    val account: Account
)
