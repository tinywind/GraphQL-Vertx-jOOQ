package kr.bigsoft.graphql_vertx_jooq.module

import dagger.Module
import dagger.Provides
import io.vertx.sqlclient.SqlClient
import kr.bigsoft.graphql_vertx_jooq.generated.tables.daos.UserDao
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import javax.inject.Singleton


@Module
class DAOModule {

    @Provides
    @Singleton
    fun provideJooQConfiguration(config: AppConfig): Configuration = DefaultConfiguration().apply {
        setSQLDialect(SQLDialect.valueOf(config.sqlDialect))
    }

    @Provides
    @Singleton
    fun provideUserDao(jooqConfig: Configuration, sqlClient: SqlClient) = UserDao(jooqConfig, sqlClient)

    // Expose other DAO classes here
}