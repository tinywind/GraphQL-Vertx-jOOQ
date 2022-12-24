# ./mvnw clean

## check read properties
```.\mvnw properties:read-project-properties@read antrun:run@check-properties```

## flyway migration
```.\mvnw properties:read-project-properties@read flyway:migrate```

## server run
```.\mvnw compile exec:java```

## ref:
* https://lorefnon.me/2021/02/01/Building-GraphQL-APIs-powered-by-Vert-x-jOOQ-Kotlin-I/
* https://lorefnon.me/2021/02/05/Building-GraphQL-APIs-powered-by-Vert-x-jOOQ-Kotlin-II/
* https://lorefnon.me/2021/03/26/Building-GraphQL-APIs-powered-by-Vert-x-jOOQ-Kotlin-III/