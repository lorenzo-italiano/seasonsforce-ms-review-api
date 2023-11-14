package fr.polytech.model

import java.util.UUID
import java.util.Date

data class Response(
    val id: UUID,
    val date: Date,
    val message: String,
    val senderId: UUID
)
