package ca.myasir.linksqueeze.dao

import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId

/**
 * This is a generic interface that will be used by other layers in the code to interact with url data.
 * Today, we're using MySQL to store this. But tomorrow we could be calling another microservice.
 * To ensure we don't have to change code in all places, we can ensure our future dao uses this interface
 */
interface ShortenedUrlDao {
    /**
     * This method will add the given shortened url data to the database
     */
    fun add(shortenedUrl: ShortenedUrl)

    /**
     * This method will return the url of the given url hash, and ensure it's not expired if expiry is present
     */
    fun get(urlHash: UrlHash): ShortenedUrl?

    /**
     * This method will return all URLs created by the given user
     */
    fun getByUser(userId: UserId): List<ShortenedUrl>

    /**
     * This method will delete the record with the given url hash for the given user. In the future, we may want to
     * consider "marking as delete" or moving to a history table as opposed to deleting the actual data
     */
    fun delete(
        urlHash: UrlHash,
        userId: UserId,
    )
}
