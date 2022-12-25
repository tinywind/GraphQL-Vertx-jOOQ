package kr.bigsoft.graphql_vertx_jooq.module

import dagger.Module
import dagger.Provides
import io.vertx.core.Vertx
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
        Vertx.vertx(),
        JDBCConnectOptions()
            .setJdbcUrl(config.url)
            .setUser(config.user)
            .setPassword(config.password),
        PoolOptions()
            .setMaxSize(16)
            .setName("pool-name")
    )
}