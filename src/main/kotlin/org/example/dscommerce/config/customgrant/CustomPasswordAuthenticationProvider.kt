package org.example.dscommerce.config.customgrant


import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.*
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator

class CustomPasswordAuthenticationProvider(
    private val authorizationService: OAuth2AuthorizationService,
    private val tokenGenerator: OAuth2TokenGenerator<out OAuth2Token>,
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val customPasswordAuthentication = authentication as CustomPasswordAuthenticationToken
        val clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthentication)
        val registeredClient = clientPrincipal.registeredClient

        if (registeredClient == null || !registeredClient.authorizationGrantTypes.contains(AuthorizationGrantType("password"))) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT)
        }

        val username = customPasswordAuthentication.username
        val password = customPasswordAuthentication.password

        val userDetails = try {
            userDetailsService.loadUserByUsername(username ?: "")
        } catch (e: UsernameNotFoundException) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT)
        }

        if (!passwordEncoder.matches(password, userDetails.password)) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT)
        }

        val authorizedScopes = registeredClient.scopes
        val customUserAuthorities = CustomUserAuthorities(userDetails.username, userDetails.authorities.toList())

        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            customUserAuthorities, null, userDetails.authorities
        )

        val tokenContext = DefaultOAuth2TokenContext.builder()
            .registeredClient(registeredClient)
            .principal(usernamePasswordAuthenticationToken)
            .authorizationServerContext(AuthorizationServerContextHolder.getContext())
            .authorizedScopes(authorizedScopes)
            .tokenType(OAuth2TokenType.ACCESS_TOKEN)
            .authorizationGrantType(AuthorizationGrantType("password"))
            .authorizationGrant(customPasswordAuthentication)
            .build()

        val generatedAccessToken = tokenGenerator.generate(tokenContext)
            ?: throw OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR)

        val accessToken = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.tokenValue,
            generatedAccessToken.issuedAt,
            generatedAccessToken.expiresAt,
            tokenContext.authorizedScopes
        )

        val authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
            .principalName(usernamePasswordAuthenticationToken.name)
            .authorizationGrantType(AuthorizationGrantType("password"))

        if (generatedAccessToken is ClaimAccessor) {
            authorizationBuilder.token(accessToken) { metadata ->
                metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = (generatedAccessToken as ClaimAccessor).claims
            }
        } else {
            authorizationBuilder.accessToken(accessToken)
        }

        val authorization = authorizationBuilder.build()
        this.authorizationService.save(authorization)

        return OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return CustomPasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    private fun getAuthenticatedClientElseThrowInvalidClient(authentication: Authentication): OAuth2ClientAuthenticationToken {
        var clientPrincipal: OAuth2ClientAuthenticationToken? = null
        if (OAuth2ClientAuthenticationToken::class.java.isAssignableFrom(authentication.principal?.javaClass)) {
            clientPrincipal = authentication.principal as OAuth2ClientAuthenticationToken
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated) {
            return clientPrincipal
        }
        throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT)
    }
}