package ca.myasir.linksqueeze.service

import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_EXPIRY
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL
import ca.myasir.linksqueeze.util.UrlHash
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

internal class UrlHashServiceTest {

    private val text = TEST_URL
    private val service = HashService()

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `it should create a hash and return`() {
        val length = 6
        val expectedHash = UrlHash("b56e91") // pre-computed

        mockkStatic(Instant::class)
        every { Instant.now() } returns Instant.EPOCH

        val result = service.createUniqueHash(text, length)

        assertEquals(expectedHash, result)
    }

    @Test
    fun `it should throw IllegalArgumentException if length is greater than 32`() {
        assertThrows<IllegalArgumentException> {
            service.createUniqueHash(text, 33)
        }
    }

    @Test
    fun `it should create unique hashes if text is different but salt is same`() {
        val text2 = "test"

        val hash1 = service.createUniqueHash(text, 6)
        val hash2 = service.createUniqueHash(text2, 6)

        assertNotEquals(hash1, hash2)
    }
}
