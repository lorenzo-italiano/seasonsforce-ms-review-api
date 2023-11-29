package fr.polytech.model

import java.util.*

class DetailedReviewDTO (
    val id: UUID,
    var grade: Int,
    var message: String,
    val sender: UserDTO,
    val user: UserDTO,
    var responseList: List<Response>,
    var date: Date,
    val offer: OfferDTO
)