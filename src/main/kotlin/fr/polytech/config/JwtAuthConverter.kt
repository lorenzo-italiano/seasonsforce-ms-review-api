package fr.polytech.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {

    private val jwtGrantedAuthoritiesConverter: JwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
    private val principleAttribute: String = System.getenv("PRINCIPLE_ATTRIBUTE_NAME")
    private val resourceId: String = System.getenv("RESOURCE_ID")

    /**
     * Convert jwt to authentication token
     *
     * @param jwt the jwt to convert
     * @return the authentication token
     */
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities: Collection<GrantedAuthority> = jwtGrantedAuthoritiesConverter.convert(jwt).orEmpty() + extractResourceRoles(jwt)

        return JwtAuthenticationToken(
            jwt,
            authorities,
            getPrincipleClaimName(jwt)
        )
    }

    /**
     * Get the principle claim name from the jwt
     *
     * @param jwt the jwt to extract the principle claim name from
     * @return the principle claim name
     */
    private fun getPrincipleClaimName(jwt: Jwt): String? {
        return jwt.getClaimAsString(principleAttribute)
    }

    /**
     * Extract resource roles from the jwt
     *
     * @param jwt the jwt to extract roles from
     * @return a collection of granted authorities
     */
    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess: Map<String, Any>? = jwt.getClaimAsMap("resource_access")
        val resource: Map<String, Any>? = resourceAccess?.get(resourceId) as? Map<String, Any>
        val resourceRoles: Collection<String>? = resource?.get("roles") as? Collection<String>

        return resourceRoles?.map { role -> SimpleGrantedAuthority("ROLE_$role") }?.toSet() ?: emptySet()
    }
}
