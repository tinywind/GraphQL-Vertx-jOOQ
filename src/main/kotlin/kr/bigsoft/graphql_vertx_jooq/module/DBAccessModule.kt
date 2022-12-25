package kr.bigsoft.graphql_vertx_jooq.module

import dagger.Module
import dagger.Provides
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.SqlClient
import javax.inject.Singleton


@Module
class DBAccessModule {

    @Provides
    @Singleton
    fun providePGClient(config: AppConfig): SqlClient = JDBCPool.pool(
        Vertx.currentContext().owner(),
        JDBCConnectOptions()
            .setJdbcUrl("jdbc:h2:file:./graphql-vertx-jooq")
            .setUser("sa")
            .setPassword(""),
        PoolOptions()
            .setMaxSize(16)
            .setName("h2")
    )
}