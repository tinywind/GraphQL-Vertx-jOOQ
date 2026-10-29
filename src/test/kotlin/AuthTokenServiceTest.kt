import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import io.jsonwebtoken.security.WeakKeyException
import kr.bigsoft.graphql_vertx_jooq.generated.tables.pojos.User
import kr.bigsoft.graphql_vertx_jooq.service.AuthTokenService
import kr.bigsoft.graphql_vertx_jooq.module.AppConfig
import java.util.Properties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(SigningKeyExtension::class)
class AuthTokenServiceTest {
    @Test
    fun `signs with the configured key and preserves login claims`() {
        val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(System.getProperty("jwt.signing.key")))
        val service = AuthTokenService(AppConfig(Properties()))
        assertNull(service.getToken(null))
        val token = assertNotNull(service.getToken(User().apply {
            name = "Maintenance"
            email = "maintenance@example.test"
        }))
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        assertEquals("Graphql-Vert.x-jOOQ", claims.issuer)
        assertEquals("user-logon", claims.subject)
        assertEquals("maintenance@example.test", claims["email"])
        assertEquals(3600000L, claims.expiration.time - claims.issuedAt.time)
        assertFailsWith<SignatureException> {
            Jwts.parser().verifyWith(Jwts.SIG.HS256.key().build()).build().parseSignedClaims(token)
        }
    }

    @Test
    fun `rejects a signing key shorter than 256 bits`() {
        System.setProperty("jwt.signing.key", Encoders.BASE64.encode(ByteArray(31)))
        assertFailsWith<WeakKeyException> { AuthTokenService(AppConfig(Properties())).getToken(User()) }
    }
}
