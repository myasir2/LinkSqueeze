package ca.myasir.linksqueeze.service

import ca.myasir.auroraweatherservice.util.toIsoFormat
import ca.myasir.linksqueeze.util.Constants.MAX_HASH_LENGTH
import ca.myasir.linksqueeze.util.UrlHash
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant

@Service
class HashService {
    /**
     * This method will hash the given text with the given salt, and return a truncated hash. It uses ISO datetime
     * from Instant.now() as the salt to ensure uniqueness
     */
    fun createUniqueHash(
        text: String,
        length: Int = 6,
    ): UrlHash {
        // MD5 returns a 32 character string
        require(length < MAX_HASH_LENGTH)

        val salt = Instant.now().toIsoFormat()
        val saltedText = "$salt$text"
        val hashBytes = MessageDigest.getInstance(HASHING_ALGORITHM).digest(saltedText.toByteArray())

        // transform to human-readable string
        val hashString = String.format("%032x", BigInteger(1, hashBytes))

        return UrlHash(hashString.take(length))
    }

    private companion object {
        /**
         * MD5 is a good hashing algorithm that is performance efficient, which we need for our use case.
         * If we were to store passwords, then we would need a stronger hashing algo since MD5 isn't too difficult
         * to crack
         */
        const val HASHING_ALGORITHM = "MD5"
    }
}
