package ca.myasir.linksqueeze.config

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.dao.ShortenedUrlDao
import ca.myasir.linksqueeze.service.HashService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class BoConfig {

    @Bean
    fun getUrlBo(hashService: HashService, shortenedUrlDao: ShortenedUrlDao): UrlBo {
        return UrlBo(hashService, shortenedUrlDao)
    }
}
