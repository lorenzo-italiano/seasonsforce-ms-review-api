package fr.polytech.model

import java.util.*

data class RecruiterDTO (
    val companyId: UUID,
    val planId: UUID,
    val offerIdList: List<UUID>,
    val paymentIdList: List<UUID>,
    override val id: UUID,
    override val email: String,
    override val firstName: String,
    override val lastName: String,
    override val username: String,
    override val role: String,
    override val isRegistered: Boolean,
    override val gender: Int,
    override val birthdate: Date,
    override val citizenship: String,
    override val phone: String,
    override val addressId: UUID,
    override val profilePictureUrl: String,
    override val toBeRemoved: Boolean
) : RecruiterCandidate(gender, birthdate, citizenship, phone, addressId, profilePictureUrl, toBeRemoved, id, email, firstName, lastName, username, role, isRegistered)