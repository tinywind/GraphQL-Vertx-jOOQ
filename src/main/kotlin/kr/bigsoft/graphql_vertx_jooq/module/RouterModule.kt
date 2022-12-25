package kr.bigsoft.graphql_vertx_jooq.module

import dagger.Module
import dagger.Provides
import graphql.GraphQL
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.graphql.GraphQLHandler
import javax.inject.Singleton

@Module
class RouterModule(private val vertx: Vertx) {

    @Provides
    @Singleton
    fun provideRouter(graphQL: GraphQL): Router =
        Router.router(vertx).also { r ->
            r.route().handler(BodyHandler.create())
            r.post("/graphql").handler(
                GraphQLHandler.create(graphQL)
            )
        }

}