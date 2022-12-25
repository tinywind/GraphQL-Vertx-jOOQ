package kr.bigsoft.graphql_vertx_jooq

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import kr.bigsoft.graphql_vertx_jooq.module.*
import org.h2.tools.Server
import java.sql.DriverManager
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
//        val appPort = components.getAppConfig().appPort
        val appPort = 8888
        vertx.createHttpServer()
            .requestHandler(component.getRouter())
            .listen(appPort) { http ->
                if (http.succeeded()) {
                    startPromise.complete()
                    println("HTTP server started on port $appPort")
                } else {
                    startPromise.fail(http.cause())
                }
            }

        val h2 = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "8090", "-webAllowOthers", "-webPort", "8091").start()
        println("H2 server on ${h2.url}")

        val conn = DriverManager.getConnection("jdbc:h2:tcp:localhost:8090/./graphql-vertx-jooq", "sa", "");
        val result = conn.prepareStatement("show tables").executeQuery()
        println("tables:")
        while(result.next()) {
            println("   ${result.getString(2)}.${result.getString(1)}")
        }
        conn.close()
    }
}

fun main(args: Array<String>) {
    println("hello! ${args.joinToString(", ")}")
}
