package ca.myasir.linksqueeze.config

import com.nimbusds.jose.shaded.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
data class AppConfig(
    @Value("\${siteUrl}")
    val siteUrl: String,
    @Value("\${auth0.tenant}")
    val auth0Tenant: String,
) {
    @Bean
    fun getGson(): Gson {
        return Gson()
    }
}
