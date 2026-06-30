package org.example.dscommerce.config



import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.example.dscommerce.config.customgrant.CustomPasswordAuthenticationConverter
import org.example.dscommerce.config.customgrant.CustomPasswordAuthenticationProvider
import org.example.dscommerce.config.customgrant.CustomUserAuthorities
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.UUID



@Configuration
class AuthorizationServerConfig(
    @Value($$"${security.client-id}") private var clientId: String,
    @Value($$"${security.client-secret}") private var clientSecret: String,
    @Value($$"${security.jwt.duration}") private var jwtDurationSeconds: Long,
    private val userDetailsService: UserDetailsService,
) {
    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun asSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {

        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer()
        val endpointsMatcher = authorizationServerConfigurer.endpointsMatcher

        http
            .securityMatcher(endpointsMatcher)
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().authenticated()
            }
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(endpointsMatcher)
            }

        authorizationServerConfigurer.tokenEndpoint { tokenEndpoint ->
            tokenEndpoint
                .accessTokenRequestConverter(CustomPasswordAuthenticationConverter())
                .authenticationProvider(
                    CustomPasswordAuthenticationProvider(
                        authorizationService(),
                        tokenGenerator(),
                        userDetailsService,
                        passwordEncoder()
                    )
                )
        }

        http.with(authorizationServerConfigurer, Customizer.withDefaults())

        http.oauth2ResourceServer { oauth2 ->
            oauth2.jwt(Customizer.withDefaults())
        }

        http.exceptionHandling { exceptions ->
            exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
        }

        return http.build()
    }

    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }

    @Bean
    fun oAuth2AuthorizationConsentService(): OAuth2AuthorizationConsentService {
        return InMemoryOAuth2AuthorizationConsentService()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(clientId)
            .clientSecret(passwordEncoder().encode(clientSecret))
            .scope("read")
            .scope("write")
            .authorizationGrantType(AuthorizationGrantType("password"))
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .tokenSettings(tokenSettings())
            .clientSettings(clientSettings())
            .build()

        return InMemoryRegisteredClientRepository(registeredClient)
    }

    @Bean
    fun tokenSettings(): TokenSettings {
        return TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds))
            .refreshTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds * 2))
            .build()
    }

    @Bean
    fun clientSettings(): ClientSettings {
        return ClientSettings.builder().build()
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().build()
    }

    @Bean
    fun tokenGenerator(): OAuth2TokenGenerator<out OAuth2Token> {
        val jwtEncoder = NimbusJwtEncoder(jwkSource())
        val jwtGenerator = JwtGenerator(jwtEncoder)
        jwtGenerator.setJwtCustomizer(tokenCustomizer())
        val accessTokenGenerator = OAuth2AccessTokenGenerator()
        val refreshTokenGenerator = OAuth2RefreshTokenGenerator()
        return DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator)
    }

    @Bean
    fun tokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context ->
            if (context.tokenType.value == "access_token") {

                val authentication = context.getPrincipal<Authentication>()

                val user = authentication.principal as CustomUserAuthorities

                val authorities = user.authorities.map { it.authority }

                context.claims
                    .claim("authorities", authorities)
                    .claim("username", user.username)
            }
        }
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey = generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    companion object {
        private fun generateRsa(): RSAKey {
            val keyPair = generateRsaKey()
            val publicKey = keyPair.public as RSAPublicKey
            val privateKey = keyPair.private as RSAPrivateKey
            return RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build()
        }

        private fun generateRsaKey(): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            return keyPairGenerator.generateKeyPair()
        }
    }
}