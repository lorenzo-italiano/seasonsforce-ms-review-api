package fr.polytech.model

import java.util.*

open class RecruiterCandidate (
    open val gender: Int,
    open val birthdate: Date,
    open val citizenship: String,
    open val phone: String,
    open val addressId: UUID,
    open val profilePictureUrl: String,
    open val toBeRemoved: Boolean,
    override val id: UUID,
    override val email: String,
    override val firstName: String,
    override val lastName: String,
    override val username: String,
    override val role: String,
    override val isRegistered: Boolean
) : UserDTO(id, email, firstName, lastName, username, role, isRegistered)