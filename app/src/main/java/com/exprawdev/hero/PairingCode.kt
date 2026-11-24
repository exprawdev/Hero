
package com.exprawdev.hero

import java.security.SecureRandom

object PairingCode {

    private val secureRandom = SecureRandom()

    fun generate(): String {
        return (100000 + secureRandom.nextInt(900000)).toString()
    }

    fun isValid(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }
}
