package fr.polytech.model.request

import java.util.*

class ResponseDTO(
    val date: Date,
    val message: String,
    val senderId: UUID
)