package kr.bigsoft.graphql_vertx_jooq.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kr.bigsoft.graphql_vertx_jooq.module.AppConfig
import io.jsonwebtoken.io.Decoders
import kr.bigsoft.graphql_vertx_jooq.generated.tables.pojos.User
import java.util.*
import javax.inject.Inject

class AuthTokenService @Inject constructor(config: AppConfig) {
    private val signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.jwtSigningKey))

    fun getToken(o: User?): String? = if (o != null)
        Jwts.builder()
            .issuer("Graphql-Vert.x-jOOQ")
            .subject("user-logon")
            .claim("name", o.name)
            .claim("email", o.email)
//        .claim("scope", "admins")
            .issuedAt(Date())
            .expiration(Date(Date().time + 60 * 60 * 1000))
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact()
    else
        null

}