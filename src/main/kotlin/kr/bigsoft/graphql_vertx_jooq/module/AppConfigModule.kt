package kr.bigsoft.graphql_vertx_jooq.module

import dagger.Module
import dagger.Provides
import java.io.FileInputStream
import java.util.*

@Module
class AppConfigModule {

    @Provides
    fun provideDatabaseConnectionConfig(): AppConfig {
        val properties = Properties()

        // Enable application consumer to inject a properties file through environment variable
        val fsPropertiesPath = System.getenv()["APP_CONFIG_PATH"]

        val propertiesStream = fsPropertiesPath
            ?.let { FileInputStream(fsPropertiesPath) }
        // Otherwise use the one in class path
            ?: javaClass.classLoader.getResourceAsStream("config.properties")

        properties.load(propertiesStream)
        return AppConfig(properties)
    }
}

/**
 * A type safe wrapper over the untyped Properties instance
 */
class AppConfig(private val properties: Properties) {
    val driver get(): String = properties.getProperty("db.driver")!!
    val url get(): String = properties.getProperty("db.url")!!
    val user get(): String? = properties.getProperty("db.user")
    val password get(): String? = properties.getProperty("db.password")
    val sqlDialect get(): String? = properties.getProperty("jooq.sql.dialect")!!
}