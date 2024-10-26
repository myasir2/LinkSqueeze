package ca.myasir.linksqueeze

import ca.myasir.linksqueeze.model.entity.ShortenedUrlEntity
import ca.myasir.linksqueeze.model.entity.UrlMetricEntity
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LinkSqueezeApplication

fun main(args: Array<String>) {
    runApplication<LinkSqueezeApplication>(*args)

    // Create database tables if not already created
    transaction {
        SchemaUtils.create(ShortenedUrlEntity, UrlMetricEntity)
    }
}
