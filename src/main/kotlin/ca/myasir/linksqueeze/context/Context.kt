package ca.myasir.linksqueeze.context

import ca.myasir.linksqueeze.util.UserId

data class Context(
    val userId: UserId
)

enum class RequestAttributeType(s: String) {
    CONTEXT("context")
}
