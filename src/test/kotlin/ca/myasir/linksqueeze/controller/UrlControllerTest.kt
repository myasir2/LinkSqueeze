package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.CreateShortenedUrlResponse
import ca.myasir.linksqueeze.test_util.TestDefaults
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_EXPIRY
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_HASH_ID
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UrlControllerTest {

    private val mockedUrlBo: UrlBo = mockk()
    private val controller = UrlController(mockedUrlBo)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `it should call Bo to create a shortened url`() {
        val request = createCreateShortenedUrlRequest()

        every { mockedUrlBo.createShortenedUrl(TEST_URL, null) } returns TEST_HASH_ID

        val response = controller.createUrl(request)
        val actualHashId = response.body?.shortenedUrl

        assertEquals(TEST_HASH_ID.value, actualHashId)
    }

    private fun createCreateShortenedUrlRequest(): CreateShortenedUrlRequest {
        return CreateShortenedUrlRequest(TEST_URL)
    }
}
