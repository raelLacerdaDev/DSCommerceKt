package org.example.dscommerce.config.customgrant

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils

class CustomPasswordAuthenticationConverter : AuthenticationConverter {

    override fun convert(request: HttpServletRequest): Authentication? {
        val grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE)

        if ("password" != grantType) {
            return null
        }

        val parameters = getParameters(request)

        val scope = parameters.getFirst(OAuth2ParameterNames.SCOPE)
        if (StringUtils.hasText(scope) && parameters[OAuth2ParameterNames.SCOPE]?.size != 1) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
        }

        val username = parameters.getFirst("username")
        if (!StringUtils.hasText(username) || parameters["username"]?.size != 1) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
        }

        val password = parameters.getFirst("password")
        if (!StringUtils.hasText(password) || parameters["password"]?.size != 1) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
        }

        var requestedScopes: Set<String>? = null
        if (StringUtils.hasText(scope)) {
            requestedScopes = HashSet(scope!!.split(" "))
        }

        val additionalParameters = HashMap<String, Any>()
        parameters.forEach { (key, value) ->
            if (key != OAuth2ParameterNames.GRANT_TYPE && key != OAuth2ParameterNames.SCOPE) {
                additionalParameters[key] = value[0]
            }
        }

        val clientPrincipal = SecurityContextHolder.getContext().authentication ?: throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
        return CustomPasswordAuthenticationToken(clientPrincipal, requestedScopes, additionalParameters)
    }

    private fun getParameters(request: HttpServletRequest): MultiValueMap<String, String> {
        val parameterMap = request.parameterMap
        val parameters: MultiValueMap<String, String> = LinkedMultiValueMap(parameterMap.size)
        parameterMap.forEach { (key, values) ->
            if (values != null) {
                for (value in values) {
                    parameters.add(key, value)
                }
            }
        }
        return parameters
    }
}