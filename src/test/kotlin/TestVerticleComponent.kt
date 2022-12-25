import dagger.Component
import io.vertx.ext.web.Router
import io.vertx.sqlclient.SqlClient
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
interface TestVerticleComponent {
    fun getRouter(): Router
    fun getAppConfig(): AppConfig
    fun getSqlClient(): SqlClient
}
