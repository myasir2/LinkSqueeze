package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.UserSavedUrl
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.DeleteUrlRequest
import ca.myasir.linksqueeze.model.response.GetUrlMetricsRequest
import ca.myasir.linksqueeze.model.response.RedirectToUrlRequest
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_URL_HASH
import ca.myasir.linksqueeze.test_util.TestDefaults.TEST_USER_ID
import ca.myasir.linksqueeze.test_util.TestDefaults.createSampleShortenedUrl
import ca.myasir.linksqueeze.test_util.TestDefaults.createSampleUrlMetric
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

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
        val expected = "${TEST_URL}/${TEST_URL_HASH.value}"

        every { mockedUrlBo.createShortenedUrl(TEST_URL, TEST_USER_ID, null) } returns TEST_URL_HASH

        val response = controller.createUrl(request)
        val actual = response.body?.shortenedUrl

        assertEquals(expected, actual)
    }

    @Test
    fun `it should call Bo to delete a shortened url`() {
        val request = createDeleteUrlRequest()

        justRun { mockedUrlBo.deleteUrl(TEST_URL_HASH, TEST_USER_ID) }

        val response = controller.deleteUrl(request)

        assertEquals(HttpStatus.OK, response.statusCode)
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

    @Test
    fun `it should return user saved urls`() {
        val urls = listOf(createSampleShortenedUrl())
        val expected = urls.map {
            UserSavedUrl(
                url = it.url,
                urlHash = it.urlHash.value,
                expiry = it.expiryDate
            )
        }

        every { mockedUrlBo.getUserSavedUrls(TEST_USER_ID) } returns urls

        val response = controller.getUserSavedUrls()
        val actual = response.body?.urls

        assertNotNull(actual)
        assertEquals(expected, actual)
    }

    @Test
    fun `it should return URL metrics for the given hash id`() {
        val request = createGetUrlMetricsRequest()
        val expected = listOf(createSampleUrlMetric())

        every { mockedUrlBo.getUrlMetrics(TEST_URL_HASH) } returns expected

        val response = controller.getUrlMetrics(request)
        val actual = response.body?.metrics

        assertNotNull(actual)
        assertEquals(expected, actual)
    }

    private fun createCreateShortenedUrlRequest(): CreateShortenedUrlRequest {
        return CreateShortenedUrlRequest(TEST_URL)
    }

    private fun createDeleteUrlRequest(): DeleteUrlRequest {
        return DeleteUrlRequest(TEST_URL_HASH.value)
    }

    private fun createRedirectToUrlRequest(): RedirectToUrlRequest {
        return RedirectToUrlRequest(TEST_URL_HASH.value)
    }

    private fun createGetUrlMetricsRequest(): GetUrlMetricsRequest {
        return GetUrlMetricsRequest(TEST_URL_HASH.value)
    }
}
