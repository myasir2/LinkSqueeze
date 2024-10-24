package ca.myasir.linksqueeze.config

import ca.myasir.linksqueeze.dao.ShortenedUrlDao
import ca.myasir.linksqueeze.dao.impl.ShortenedUrlDaoImpl
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class DaoConfig(private val dataSource: DataSource) {

    @PostConstruct
    fun connectDatabase() {
        Database.connect(dataSource)
    }

    @Bean
    fun getShortenedUrlDao(): ShortenedUrlDao {
        return ShortenedUrlDaoImpl()
    }
}
