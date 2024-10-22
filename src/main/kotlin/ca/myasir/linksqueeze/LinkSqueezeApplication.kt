package ca.myasir.linksqueeze

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LinkSqueezeApplication

fun main(args: Array<String>) {
    Database.connect(
        url = "jdbc:mariadb://localhost:3306/link_squeeze",
        driver = "org.mariadb.jdbc.Driver",
        user = "root",
        password = "my-secret-pw"
    )

	runApplication<LinkSqueezeApplication>(*args)
}
