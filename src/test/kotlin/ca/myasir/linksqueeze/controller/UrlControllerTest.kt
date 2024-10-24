package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.CreateShortenedUrlResponse
import ca.myasir.linksqueeze.model.response.DeleteUrlRequest
import ca.myasir.linksqueeze.model.response.RedirectToUrlRequest
import ca.myasir.linksqueeze.test_util.TestDefaults
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_EXPIRY
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_HASH_ID
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_USER_ID
import ca.myasir.linksqueeze.test_util.TestDefaults.createSampleShortenedUrl
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

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

    @Test
    fun `it should call Bo to delete a shortened url`() {
        val request = createDeleteUrlRequest()

        justRun { mockedUrlBo.deleteUrl(TEST_HASH_ID, TEST_USER_ID) }

        val response = controller.deleteUrl(request)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `it should redirect to the url for the given hash`() {
        val request = createRedirectToUrlRequest()
        val expectedShortenedUrl = createSampleShortenedUrl()

        every { mockedUrlBo.getUrl(TEST_HASH_ID) } returns expectedShortenedUrl

        val response = controller.redirectToUrl(request)
        val actualShortenedUrl = response.url

        assertEquals(expectedShortenedUrl.url, actualShortenedUrl)
    }

    @Test
    fun `it should throw NotFoundException if shortened url is null`() {
        val request = createRedirectToUrlRequest()

        every { mockedUrlBo.getUrl(TEST_HASH_ID) } returns null

        assertThrows<ResourceNotFoundException> {
            controller.redirectToUrl(request)
        }
    }

    private fun createCreateShortenedUrlRequest(): CreateShortenedUrlRequest {
        return CreateShortenedUrlRequest(TEST_URL)
    }

    private fun createDeleteUrlRequest(): DeleteUrlRequest {
        return DeleteUrlRequest(TEST_HASH_ID.value)
    }

    private fun createRedirectToUrlRequest(): RedirectToUrlRequest {
        return RedirectToUrlRequest(TEST_HASH_ID.value)
    }
}
