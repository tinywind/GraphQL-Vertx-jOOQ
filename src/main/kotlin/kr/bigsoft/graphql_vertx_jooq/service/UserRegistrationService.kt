package kr.bigsoft.graphql_vertx_jooq.service

import io.vertx.core.Future
import kr.bigsoft.graphql_vertx_jooq.generated.tables.daos.UserDao
import kr.bigsoft.graphql_vertx_jooq.generated.tables.pojos.User
import javax.inject.Inject

class UserRegistrationService @Inject constructor(
    private val userDao: UserDao,
    private val passwordEncryptionService: PasswordEncryptionService
) {
    fun registerUser(name: String, email: String, password: String): Future<RegistrationResult> {
        return userDao.insert(User().apply {
            this.name = name
            this.password = passwordEncryptionService.encrypt(password)
            this.email = email
        }).map { insertCount ->
            RegistrationResult(true)
        }.otherwise { throwable ->
            RegistrationResult(false, throwable.message?.let {
                if (throwable.message?.contains("violates unique constraint") == true)
                    listOf(
                        "A user is already registered for this email/username",
                        "Do you want to sign in instead ?"
                    )
                else listOf(throwable.message!!)
            })
        }
    }
}

data class RegistrationResult(
    val success: Boolean,
    val comments: List<String>? = null
)