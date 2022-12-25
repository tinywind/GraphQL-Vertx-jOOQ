package kr.bigsoft.graphql_vertx_jooq.service

import at.favre.lib.crypto.bcrypt.BCrypt
import javax.inject.Inject

private const val LOGARITHMIC_COST_FACTOR = 12

class PasswordEncryptionService @Inject constructor() {

    fun encrypt(password: String) =
        BCrypt.withDefaults().hashToString(LOGARITHMIC_COST_FACTOR, password.toCharArray())

    fun verify(password: String, encryptedPassword: String) =
        BCrypt.verifyer().verify(password.toCharArray(), encryptedPassword).verified
}