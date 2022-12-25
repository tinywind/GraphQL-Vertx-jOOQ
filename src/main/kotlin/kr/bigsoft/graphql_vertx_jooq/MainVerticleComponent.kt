package kr.bigsoft.graphql_vertx_jooq

import dagger.Component
import io.vertx.ext.web.Router
import kr.bigsoft.graphql_vertx_jooq.module.*
import javax.inject.Singleton

@Component(
    modules = [
        AppConfigModule::class,
        DBAccessModule::class,
        DAOModule::class,
        GraphQLModule::class,
        RouterModule::class
    ]
)
@Singleton
interface MainVerticleComponent {
    fun getRouter(): Router
}
