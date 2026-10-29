import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.Tuple
import io.jsonwebtoken.io.Decoders
import kr.bigsoft.graphql_vertx_jooq.module.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(SigningKeyExtension::class)
class GraphQLHttpTest {
    @Test
    fun `serves GraphQL registration and signed login over HTTP`() {
        val vertx = Vertx.vertx()
        val component = DaggerTestVerticleComponent.builder()
            .appConfigModule(AppConfigModule())
            .dBAccessModule(DBAccessModule())
            .dAOModule(DAOModule())
            .graphQLModule(GraphQLModule())
            .routerModule(RouterModule(vertx))
            .build()
        assertTrue(component.getAppConfig().url.startsWith("jdbc:h2:file:./"))
        val email = "maintenance-${UUID.randomUUID()}@example.test"
        val server = vertx.createHttpServer().requestHandler(component.getRouter())
            .listen(0, "127.0.0.1").toCompletionStage().toCompletableFuture().get(10, TimeUnit.SECONDS)
        try {
            val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()
            fun query(query: String): JsonObject {
                val response = client.send(
                    HttpRequest.newBuilder(URI("http://127.0.0.1:${server.actualPort()}/graphql"))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(JsonObject().put("query", query).encode()))
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                )
                assertEquals(200, response.statusCode())
                val body = JsonObject(response.body())
                assertNull(body.getJsonArray("errors"), response.body())
                return body.getJsonObject("data")
            }
            assertEquals("world", query("{ hello }").getString("hello"))
            val registered = query("""mutation { registerUser(name: "Maintenance", email: "$email", password: "test-password") { success } }""")
            assertTrue(registered.getJsonObject("registerUser").getBoolean("success"))
            assertNull(query("""mutation { login(email: "$email", password: "wrong-password") }""").getString("login"))
            val token = assertNotNull(query("""mutation { login(email: "$email", password: "test-password") }""").getString("login"))
            val parts = token.split('.')
            assertEquals(3, parts.size)
            assertTrue(Decoders.BASE64URL.decode(parts[2]).isNotEmpty())
            val header = JsonObject(String(Decoders.BASE64URL.decode(parts[0]), Charsets.UTF_8))
            val claims = JsonObject(String(Decoders.BASE64URL.decode(parts[1]), Charsets.UTF_8))
            assertEquals("HS256", header.getString("alg"))
            assertEquals("Graphql-Vert.x-jOOQ", claims.getString("iss"))
            assertEquals("user-logon", claims.getString("sub"))
            assertEquals("Maintenance", claims.getString("name"))
            assertEquals(email, claims.getString("email"))
            assertEquals(3600L, claims.getLong("exp") - claims.getLong("iat"))
            assertNull(claims.getString("password"))
        } finally {
            component.getSqlClient().preparedQuery("DELETE FROM \"user\" WHERE email = ?")
                .execute(Tuple.of(email)).toCompletionStage().toCompletableFuture().get(10, TimeUnit.SECONDS)
            server.close().toCompletionStage().toCompletableFuture().get(10, TimeUnit.SECONDS)
            component.getSqlClient().close().toCompletionStage().toCompletableFuture().get(10, TimeUnit.SECONDS)
            vertx.close().toCompletionStage().toCompletableFuture().get(10, TimeUnit.SECONDS)
        }
    }
}
