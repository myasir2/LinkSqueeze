package ca.myasir.linksqueeze.context

import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.model.User
import ca.myasir.linksqueeze.util.Constants.GENERATE_URL_ROUTE
import ca.myasir.linksqueeze.util.UserId
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.nimbusds.jose.shaded.gson.Gson
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.security.InvalidParameterException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthFilter(

    appConfig: AppConfig,
    private val gson: Gson
): Filter {

    private val bearerTokenPrefix = "Bearer "
    private val auth0Tenant = appConfig.auth0Tenant
    private val jwkProvider = UrlJwkProvider(appConfig.auth0Tenant)
    private val keyIdToPublicKeyMap = mutableMapOf<String, RSAPublicKey>()
    private val logger = KotlinLogging.logger {}
    private val allowedUnauthenticatedPaths = setOf(
        GENERATE_URL_ROUTE
    )

    /**
     * If an access token is given, then get the user information from Auth0 and put it in the Context
     */
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpServletRequest = request as HttpServletRequest
        val httpServletResponse = response as HttpServletResponse
        val authorizationHeaderValue = httpServletRequest.getHeader("Authorization")

        // Check if request is for unauthenticated paths such as health endpoint
        allowedUnauthenticatedPaths.forEach { path ->
            val uri = httpServletRequest.requestURI

            // We want to ensure the redirect path "/" stays unauthenticated because otherwise every url will start
            // with "/"
            if (uri.matches(Regex("^/[^/]+$")) || uri.startsWith(path)) {
                chain!!.doFilter(httpServletRequest, httpServletResponse)

                return
            }
        }

        if(authorizationHeaderValue == null || !authorizationHeaderValue.startsWith(bearerTokenPrefix)) {
            setUnauthorizedResponse(httpServletResponse)

            return
        }

        // Extract and decode JWT
        val token = authorizationHeaderValue.replace(bearerTokenPrefix, "")
        val user = getUser(token)

        if(user == null) {
            setUnauthorizedResponse(httpServletResponse)

            return
        }

        try {
            // Extract and decode JWT
            val decodedJWT = decodeJwt(token)

            // Initialize public key and algorithm
            val publicKey = getPublicKey(decodedJWT)
            val algorithm = Algorithm.RSA256(publicKey, null as RSAPrivateKey?)

            // Verify JWT
            val verifier = JWT.require(algorithm)
                .withIssuer(decodedJWT.issuer)
                .build()
            verifier.verify(decodedJWT)

            // Create and set the context to request attribute
            val context = Context(UserId(user.sub))
            httpServletRequest.setAttribute(RequestAttributeType.CONTEXT.toString(), context)

            logger.info { user }

            chain!!.doFilter(httpServletRequest, httpServletResponse)
        }
        catch (e: Exception) {
            logger.info { "Exception in AuthFilter: " + e.message }

            setUnauthorizedResponse(httpServletResponse)

            return
        }
    }

    private fun getUser(accessToken: String): User? {
        // Create and set the "Authorization" header before sending HTTP request
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer $accessToken")
        val entity = HttpEntity(null, headers)

        // Use the "RestTemplate" API provided by Spring to make the HTTP request
        val restTemplate = RestTemplate()

        return restTemplate.exchange(
            "${auth0Tenant}userinfo", HttpMethod.POST, entity,
            User::class.java
        ).body
    }

    private fun getPublicKey(token: DecodedJWT): RSAPublicKey {
        return keyIdToPublicKeyMap.getOrElse(token.keyId, {
            val key = jwkProvider.get(token.keyId).publicKey as RSAPublicKey

            keyIdToPublicKeyMap[token.keyId] = key

            return key
        })
    }

    private fun decodeJwt(token: String): DecodedJWT {
        return JWT.decode(token) ?: throw InvalidParameterException("Invalid jwt: $token")
    }

    private fun setUnauthorizedResponse(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(
            gson.toJson(mapOf(
                "message" to "Unauthorized"
            ))
        )
    }
}
