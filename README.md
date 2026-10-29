# GraphQL-Vertx-jOOQ

Requires JDK 21. Run `./mvnw verify` to apply the local H2 migrations, generate DAOs, compile the sources, run the tests, and package the executable JAR.

The application uses the patched Vert.x 4.5 line. Its Vert.x-jOOQ 6.5.5 generator targets jOOQ 3.17, so the build uses the latest compatible 3.17.35 patch; jOOQ 3.21 fails during DAO generation. Netty, Jackson, and Kotlin dependency management override older transitive releases.

The HTTP test covers the hello query, registration, bcrypt password checks, and login token claims. It uses a unique test email in the local H2 database and removes that record afterward.

Set `JWT_SIGNING_KEY` to a Base64-encoded random key of at least 32 bytes before starting the server. An explicit `jwt.signing.key` JVM property or application property is also accepted. There is no built-in signing key. Tests generate and discard their own keys.

GraphQL Java 20.9 bundles a vulnerable private copy of Guava. Maven extracts its unchanged official sources and resources, compiles them against Guava 33.7.2, and excludes the old binary. The upstream MIT license is packaged under `META-INF/licenses/`. This preserves the current GraphQL API without retaining the vulnerable shaded classes. The source artifact SHA-256 is `2f24633a971e61a671bfb428ebf12c2da045453e002cc297bf12abf7a9009f31`.

## flyway migration
```.\mvnw properties:read-project-properties@read flyway:migrate```

## server run
```.\mvnw compile exec:java```

## ref:
* https://lorefnon.me/2021/02/01/Building-GraphQL-APIs-powered-by-Vert-x-jOOQ-Kotlin-I/
* https://lorefnon.me/2021/02/05/Building-GraphQL-APIs-powered-by-Vert-x-jOOQ-Kotlin-II/
* https://lorefnon.me/2021/03/26/Building-GraphQL-APIs-powered-by-Vert-x-jOOQ-Kotlin-III/