package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.context.Context
import ca.myasir.linksqueeze.context.RequestAttributeType
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.UrlDetails
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.DeleteUrlRequest
import ca.myasir.linksqueeze.model.response.GetUrlMetricsRequest
import ca.myasir.linksqueeze.testutil.TestDefaults.TEST_URL
import ca.myasir.linksqueeze.testutil.TestDefaults.TEST_URL_HASH
import ca.myasir.linksqueeze.testutil.TestDefaults.TEST_USER_ID
import ca.myasir.linksqueeze.testutil.TestDefaults.createSampleShortenedUrl
import ca.myasir.linksqueeze.testutil.TestDefaults.createSampleUrlDetails
import ca.myasir.linksqueeze.testutil.TestDefaults.createSampleUrlMetric
import ca.myasir.linksqueeze.util.UrlHash
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

internal class UserControllerTest {
    private val mockedServletRequest: HttpServletRequest = mockk()
    private val mockedUrlBo: UrlBo = mockk()
    private val mockedAppConfig: AppConfig = mockk()
    private val controller = UserController(mockedAppConfig, mockedUrlBo)
    private val context = Context(TEST_USER_ID)

    @BeforeEach
    fun setup() {
        clearAllMocks()

        every { mockedAppConfig.siteUrl } returns TEST_URL
        every {
            mockedServletRequest.getAttribute(RequestAttributeType.CONTEXT.toString())
        }.returns(context)
    }

    @Test
    fun `it should call Bo to create a shortened url`() {
        val request = createCreateShortenedUrlRequest()
        val shortenedUrl = createSampleShortenedUrl()
        val expected = createSampleUrlDetails()

        every { mockedUrlBo.createShortenedUrl(TEST_URL, context.userId, null) } returns shortenedUrl

        val response = controller.createUrl(mockedServletRequest, request)
        val actual = response.body?.urlDetails

        assertEquals(expected, actual)
    }

    @Test
    fun `it should call Bo to delete a shortened url`() {
        val request = createDeleteUrlRequest()
        val existingUrls = listOf(createSampleShortenedUrl())

        every { mockedUrlBo.getUserSavedUrls(context.userId) } returns existingUrls
        justRun { mockedUrlBo.deleteUrl(TEST_URL_HASH, context.userId) }

        val response = controller.deleteUrl(mockedServletRequest, request)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `it should throw NotFoundException if shortened url not found for the given user id when deleting`() {
        val request = createDeleteUrlRequest()
        val existingUrls = emptyList<ShortenedUrl>()

        every { mockedUrlBo.getUserSavedUrls(context.userId) } returns existingUrls

        assertThrows<ResourceNotFoundException> {
            controller.deleteUrl(mockedServletRequest, request)
        }

        verify(exactly = 0) {
            mockedUrlBo.deleteUrl(any(), any())
        }
    }

    @Test
    fun `it should throw NotFoundException if url for the given hash not found amongst user saved urls when deleting`() {
        val request = createDeleteUrlRequest()
        val existingUrls = listOf(createSampleShortenedUrl(urlHash = UrlHash("somethingdifference")))

        every { mockedUrlBo.getUserSavedUrls(context.userId) } returns existingUrls

        assertThrows<ResourceNotFoundException> {
            controller.deleteUrl(mockedServletRequest, request)
        }

        verify(exactly = 0) {
            mockedUrlBo.deleteUrl(any(), any())
        }
    }

    @Test
    fun `it should return user saved urls`() {
        val urls = listOf(createSampleShortenedUrl())
        val expected =
            urls.map {
                UrlDetails(
                    originalUrl = it.url,
                    shortenedUrl = "$TEST_URL/${it.urlHash.value}",
                    expiry = it.expiryDate,
                )
            }

        every { mockedUrlBo.getUserSavedUrls(context.userId) } returns urls

        val response = controller.getUserSavedUrls(mockedServletRequest)
        val actual = response.body?.urls

        assertNotNull(actual)
        assertEquals(expected, actual)
    }

    @Test
    fun `it should return URL metrics for the given hash id`() {
        val request = createGetUrlMetricsRequest()
        val expected = listOf(createSampleUrlMetric())
        val existingUrls = listOf(createSampleShortenedUrl())

        every { mockedUrlBo.getUserSavedUrls(context.userId) } returns existingUrls
        every { mockedUrlBo.getUrlMetrics(TEST_URL_HASH) } returns expected

        val response = controller.getUrlMetrics(mockedServletRequest, request)
        val actual = response.body?.metrics

        assertNotNull(actual)
        assertEquals(expected, actual)
    }

    @Test
    fun `it should throw NotFoundException if URL doesn't exist for the given user id and hash`() {
        val request = createGetUrlMetricsRequest()
        val existingUrls =
            listOf(
                emptyList(),
                listOf(createSampleShortenedUrl(urlHash = UrlHash("somethingdifference"))),
            )

        for (url in existingUrls) {
            every { mockedUrlBo.getUserSavedUrls(context.userId) } returns url

            assertThrows<ResourceNotFoundException> {
                controller.getUrlMetrics(mockedServletRequest, request)
            }

            verify(exactly = 0) {
                mockedUrlBo.getUrlMetrics(TEST_URL_HASH)
            }
        }
    }

    private fun createCreateShortenedUrlRequest(): CreateShortenedUrlRequest {
        return CreateShortenedUrlRequest(TEST_URL)
    }

    private fun createDeleteUrlRequest(): DeleteUrlRequest {
        return DeleteUrlRequest(TEST_URL_HASH.value)
    }

    private fun createGetUrlMetricsRequest(): GetUrlMetricsRequest {
        return GetUrlMetricsRequest(TEST_URL_HASH.value)
    }
}
