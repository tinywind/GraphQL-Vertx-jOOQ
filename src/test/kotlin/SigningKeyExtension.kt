import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class SigningKeyExtension : BeforeEachCallback, AfterEachCallback {
    private var previous: String? = null

    override fun beforeEach(context: ExtensionContext) {
        previous = System.getProperty("jwt.signing.key")
        System.setProperty("jwt.signing.key", Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().encoded))
    }

    override fun afterEach(context: ExtensionContext) {
        previous?.let { System.setProperty("jwt.signing.key", it) }
            ?: System.clearProperty("jwt.signing.key")
    }
}
