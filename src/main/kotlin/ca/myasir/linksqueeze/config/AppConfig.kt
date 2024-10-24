package ca.myasir.linksqueeze.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class AppConfig(

    @Value("\${siteUrl}")
    val siteUrl: String,
)
