package kr.bigsoft.graphql_vertx_jooq

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.graphql.GraphQLHandler

@Suppress("unused")
class MainVerticle : AbstractVerticle() {
    override fun start(startPromise: Promise<Void>) {
        val router = setupRouter(vertx) // More on this below
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888) { http ->
                if (http.succeeded()) {
                    startPromise.complete()
                    println("HTTP server started on port 8888")
                } else {
                    startPromise.fail(http.cause())
                }
            }
    }

    fun setupRouter(vertx: Vertx) =
        Router.router(vertx).also { r ->
            r.route().handler(BodyHandler.create())
            r.post("/graphql").handler(
                GraphQLHandler.create(
                    setupGraphQL() // More on this below
                )
            )
        }

    fun setupGraphQL() = GraphQL
        .newGraphQL(buildExecutableSchema())
        .build()

    val rawSchema = """
    type Query {
        hello: String
    }
""".trimIndent()

    fun buildRuntimeWiring() = newRuntimeWiring()
        .type("Query") {
            it.dataFetcher("hello") {
                // Here is the logic for resolving our hello field in Query type
                // For now we just return a static string
                "world"
            }
        }
        .build()

    fun buildExecutableSchema() = SchemaGenerator().makeExecutableSchema(parseSchema(), buildRuntimeWiring())

    fun parseSchema() = SchemaParser().parse(rawSchema)
}

fun main(args: Array<String>) {
    println("hello! ${args.joinToString(", ")}")
}
