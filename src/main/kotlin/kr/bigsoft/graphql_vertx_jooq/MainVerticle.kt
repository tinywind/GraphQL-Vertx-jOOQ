package kr.bigsoft.graphql_vertx_jooq

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import kr.bigsoft.graphql_vertx_jooq.module.*
import java.util.*

@Suppress("unused")
class MainVerticle : AbstractVerticle() {

    private val component by lazy {
        DaggerMainVerticleComponent
            .builder()
            .appConfigModule(AppConfigModule())
            .dBAccessModule(DBAccessModule())
            .dAOModule(DAOModule())
            .graphQLModule(GraphQLModule())
            .routerModule(RouterModule(vertx))
            .build()
    }

    override fun start(startPromise: Promise<Void>) {
        vertx.createHttpServer()
            .requestHandler(component.getRouter())
            .listen(component.getAppConfig().appPort) { http ->
                if (http.succeeded()) {
                    startPromise.complete()
                    println("HTTP server started on port ${component.getAppConfig().appPort}")
                } else {
                    startPromise.fail(http.cause())
                }
            }
    }
}

fun main(args: Array<String>) {
    println("hello! ${args.joinToString(", ")}")
}
