package kr.bigsoft.graphql_vertx_jooq.service

import io.vertx.core.Future
import kr.bigsoft.graphql_vertx_jooq.generated.tables.daos.UserDao
import javax.inject.Inject

class UserLoginService @Inject constructor(
    private val userDao: UserDao,
    private val passwordEncryptionService: PasswordEncryptionService,
    private val authTokenService: AuthTokenService
) {
    fun login(email: String, password: String): Future<String?> = userDao.findOneById(email).map {
        it?.let { user ->
            if (passwordEncryptionService.verify(password, user.password)) user
            else null
        }?.let { user ->
            if (user != null) {
                user.password = null
            } else {
                // RegistrationResult(false, listOf("unknown user"))
            }
            authTokenService.getToken(user)
        }
    }
}
