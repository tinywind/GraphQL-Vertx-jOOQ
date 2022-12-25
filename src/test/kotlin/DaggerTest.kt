import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.sqlclient.SqlClient
import kr.bigsoft.graphql_vertx_jooq.MainVerticle
import kr.bigsoft.graphql_vertx_jooq.module.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@Suppress("JUnitMalformedDeclaration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension::class)
class DaggerTest {
    var component: DaggerTestVerticleComponent? = null

    @BeforeEach
    fun setup(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(MainVerticle(), testContext.succeedingThenComplete())
        component = DaggerTestVerticleComponent
            .builder()
            .appConfigModule(AppConfigModule())
            .dBAccessModule(DBAccessModule())
            .dAOModule(DAOModule())
            .graphQLModule(GraphQLModule())
            .routerModule(RouterModule(vertx))
            .build() as DaggerTestVerticleComponent?
    }

    @Test
    fun `check properties`(vertx: Vertx, testContext: VertxTestContext) {
        assertEquals(component?.getAppConfig()?.appPort, 9000)
        testContext.completeNow()
    }

    @Test
    fun `check db connection`(vertx: Vertx, testContext: VertxTestContext) {
        val client = component?.getSqlClient() as SqlClient
        client.preparedQuery("show tables").execute().map { it ->
            println("check db connection 3")
            println("tables:")
            it.forEach {
                println("   ${it.getString(1)}.${it.getString(0)}")
            }
            testContext.completeNow()
        }
    }
}
