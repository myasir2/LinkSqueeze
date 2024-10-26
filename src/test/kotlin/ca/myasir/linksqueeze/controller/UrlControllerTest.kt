package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.RedirectToUrlRequest
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL_HASH
import ca.myasir.linksqueeze.test_util.TestDefaults.createSampleShortenedUrl
import ca.myasir.linksqueeze.test_util.TestDefaults.createSampleUrlDetails
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UrlControllerTest {

    private val mockedUrlBo: UrlBo = mockk()
    private val mockedAppConfig: AppConfig = mockk()
    private val controller = UrlController(mockedAppConfig, mockedUrlBo)

    @BeforeEach
    fun setup() {
        clearAllMocks()

        every { mockedAppConfig.siteUrl } returns TEST_URL
    }

    @Test
    fun `it should call Bo to create a shortened url`() {
        val request = createCreateShortenedUrlRequest()
        val shortenedUrl = createSampleShortenedUrl()
        val expected = createSampleUrlDetails()

        every { mockedUrlBo.createShortenedUrl(TEST_URL, null, null) } returns shortenedUrl

        val response = controller.createUrl(request)
        val actual = response.body?.urlDetails

        assertEquals(expected, actual)
    }

    @Test
    fun `it should redirect to the url for the given hash`() {
        val request = createRedirectToUrlRequest()
        val expectedShortenedUrl = createSampleShortenedUrl()

        every { mockedUrlBo.getUrl(TEST_URL_HASH) } returns expectedShortenedUrl

        val response = controller.redirectToUrl(request)
        val actualShortenedUrl = response.url

        assertEquals(expectedShortenedUrl.url, actualShortenedUrl)
    }

    @Test
    fun `it should throw NotFoundException if shortened url is null`() {
        val request = createRedirectToUrlRequest()

        every { mockedUrlBo.getUrl(TEST_URL_HASH) } returns null

        assertThrows<ResourceNotFoundException> {
            controller.redirectToUrl(request)
        }
    }

    private fun createCreateShortenedUrlRequest(): CreateShortenedUrlRequest {
        return CreateShortenedUrlRequest(TEST_URL)
    }

    private fun createRedirectToUrlRequest(): RedirectToUrlRequest {
        return RedirectToUrlRequest(TEST_URL_HASH.value)
    }
}
