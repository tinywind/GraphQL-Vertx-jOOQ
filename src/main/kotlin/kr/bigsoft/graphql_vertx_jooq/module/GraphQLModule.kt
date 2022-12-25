package kr.bigsoft.graphql_vertx_jooq.module

import dagger.Module
import dagger.Provides
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.vertx.core.Vertx
import io.vertx.ext.web.handler.graphql.schema.VertxDataFetcher
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.PoolOptions
import kr.bigsoft.graphql_vertx_jooq.service.UserLoginService
import kr.bigsoft.graphql_vertx_jooq.service.UserRegistrationService
import javax.inject.Singleton


@Module
class GraphQLModule {

    @Provides
    @Singleton
    fun provideGraphQL(
        userRegistrationService: UserRegistrationService,
        userLoginService: UserLoginService
    ) = GraphQLBuilder(
        userRegistrationService,
        userLoginService
    ).build()

    inner class GraphQLBuilder(
        private val userRegistrationService: UserRegistrationService,
        private val userLoginService: UserLoginService
    ) {
        fun build(): GraphQL = GraphQL
            .newGraphQL(buildExecutableSchema())
            .build()

        private val rawSchema = """
            type Query {
                hello: String
            }
    
            type Mutation {
                registerUser(
                    name: String!, 
                    email: String!, 
                    password: String!
                ): RegistrationResult!

                login(email: String!, password: String!): String
            }
    
            type RegistrationResult {
                success: Boolean!
                comments: [String]
            }

        """.trimIndent()

        private fun buildRuntimeWiring() = newRuntimeWiring()
            .type("Query") {
                it.dataFetcher("hello") { "world" }
            }
            .type("Mutation") {
                it.associateRegisterUserMutation()
                it.associateLoginMutation()
            }
            .build()

        private fun TypeRuntimeWiring.Builder.associateRegisterUserMutation() =
            dataFetcher("registerUser", VertxDataFetcher.create { env ->
                val name = env.getArgument<String>("name")
                val email = env.getArgument<String>("email")
                val password = env.getArgument<String>("password")
                userRegistrationService.registerUser(name, email, password)
            })

        private fun TypeRuntimeWiring.Builder.associateLoginMutation() =
            dataFetcher("login", VertxDataFetcher.create { env ->
                val email = env.getArgument<String>("email")
                val password = env.getArgument<String>("password")
                userLoginService.login(email, password)
            })

        private fun buildExecutableSchema() = SchemaGenerator().makeExecutableSchema(parseSchema(), buildRuntimeWiring())

        private fun parseSchema() = SchemaParser().parse(rawSchema)
    }
}