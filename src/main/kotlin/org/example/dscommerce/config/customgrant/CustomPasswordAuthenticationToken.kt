package org.example.dscommerce.config.customgrant

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken
import java.util.Collections

class CustomPasswordAuthenticationToken(
    clientPrincipal: Authentication,
    scopes: Set<String>?,
    additionalParameters: Map<String, Any>?
): OAuth2AuthorizationGrantAuthenticationToken(
    AuthorizationGrantType("password"),
    clientPrincipal,
    additionalParameters
) {

    val username: String? = additionalParameters?.get("username") as String?
    val password: String? = additionalParameters?.get("password") as String?
    val scopes: Set<String> = if (scopes != null) Collections.unmodifiableSet(HashSet(scopes)) else Collections.emptySet()

    companion object {
        private const val serialVersionUID = 1L
    }
}