package ca.myasir.linksqueeze.config

import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DaoConfig(private val dataSource: DataSource) {
    @PostConstruct
    fun connectDatabase() {
        Database.connect(dataSource)
    }
}
