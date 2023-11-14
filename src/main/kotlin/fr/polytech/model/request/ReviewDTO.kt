package fr.polytech.model.request

import java.util.*

class ReviewDTO (
    val grade: Int,
    val message: String,
    val senderId: UUID
)