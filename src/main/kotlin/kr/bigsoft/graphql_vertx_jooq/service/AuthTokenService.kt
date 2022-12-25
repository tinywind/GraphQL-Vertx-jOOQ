package kr.bigsoft.graphql_vertx_jooq.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.TextCodec
import kr.bigsoft.graphql_vertx_jooq.generated.tables.pojos.User
import java.util.*
import javax.inject.Inject

class AuthTokenService @Inject constructor() {
    fun getToken(o: User?): String? = if (o != null)
        Jwts.builder()
            .setIssuer("Graphql-Vert.x-jOOQ")
            .setSubject("user-logon")
            .claim("name", o.name)
            .claim("email", o.email)
//        .claim("scope", "admins")
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + 60 * 60 * 1000))
            .signWith(
                SignatureAlgorithm.HS256,
                TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")
            )
            .compact()
    else
        null

}