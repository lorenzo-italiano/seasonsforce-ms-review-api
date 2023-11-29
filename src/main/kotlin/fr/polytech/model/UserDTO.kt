package fr.polytech.model

import java.util.*

open class UserDTO (
    open val id: UUID,
    open val email: String,
    open val firstName: String,
    open val lastName: String,
    open val username: String,
    open val role: String,
    open val isRegistered: Boolean
)