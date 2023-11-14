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
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities: Collection<GrantedAuthority> = jwtGrantedAuthoritiesConverter.convert(jwt).orEmpty() + extractResourceRoles(jwt)

        return JwtAuthenticationToken(
            jwt,
            authorities,
            getPrincipleClaimName(jwt)
        )
    }

    private fun getPrincipleClaimName(jwt: Jwt): String? {
        return jwt.getClaimAsString(principleAttribute)
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess: Map<String, Any>? = jwt.getClaimAsMap("resource_access")
        val resource: Map<String, Any>? = resourceAccess?.get(resourceId) as? Map<String, Any>
        val resourceRoles: Collection<String>? = resource?.get("roles") as? Collection<String>

        return resourceRoles?.map { role -> SimpleGrantedAuthority("ROLE_$role") }?.toSet() ?: emptySet()
    }
}
